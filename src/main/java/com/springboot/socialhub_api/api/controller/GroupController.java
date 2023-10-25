package com.springboot.socialhub_api.api.controller;

import com.springboot.socialhub_api.api.model.Group;
import com.springboot.socialhub_api.api.repositories.GroupRepository;
import com.springboot.socialhub_api.api.repositories.PostRepository;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import com.springboot.socialhub_api.api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.model.Post;
import org.springframework.http.HttpStatus;

import java.util.*;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/group")
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final AuthService authService;

    GroupController(GroupRepository groupRepository, UserRepository userRepository, PostRepository postRepository,AuthService authService) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.authService = authService;
    }

    // Get a group by group_id
    @GetMapping("/{group_id}")
    public ResponseEntity<Optional<Group>> getGroup(@RequestHeader("Authorization")String token,@PathVariable("group_id") int groupId) {
        if(authService.isLoggedIn(token)){
            Optional<Group> group = groupRepository.findById(groupId);
            if (group.isPresent()) {
                return ResponseEntity.ok(group);
            } else {
                return ResponseEntity.notFound().build();
            }
        }else{
            return null;
        }
    }

    // Get all groups
    @GetMapping("/all")
    public List<Group> getAllGroups(@RequestHeader("Authorization")String token) {
        if(authService.isLoggedIn(token)){
            return groupRepository.findAll();
        }else{
            return null;
        }
    }

    // Create a group
    @PostMapping("/{user_id}")
    public ResponseEntity<Group> createGroup(@RequestHeader("Authorization")String token,@PathVariable("user_id") int userId,
            @RequestBody Map<String, String> groupData) {
        if(authService.isLoggedIn(token)){
            Optional<User> user = userRepository.findById(userId);

            if (user.isPresent()) {
                String name = groupData.get("name");
                String description = groupData.get("description");
                String coverPicture = groupData.get("coverPicture");

                Group newGroup = new Group(name, description, coverPicture, new Date());
                newGroup.setOwner_id(userId);
                newGroup.getMembers().add(user.get()); // Dodaj właściciela jako członka
                Group createdGroup = groupRepository.save(newGroup);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
            } else {
                // Obsłuż przypadek, gdy użytkownik o user_id nie istnieje
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }else{
            return null;
        }
    }

    // Update a group
    @PutMapping("/{group_id}")
    public ResponseEntity<Group> updateGroup(@RequestHeader("Authorization")String token,@PathVariable("group_id") int groupId,
            @RequestBody Map<String, String> groupData) {
        if(authService.isLoggedIn(token)){
            Optional<Group> group = groupRepository.findById(groupId);
            if (group.isPresent()) {
                Group existingGroup = group.get();
                if (groupData.containsKey("name")) {
                    existingGroup.setName(groupData.get("name"));
                }
                if (groupData.containsKey("description")) {
                    existingGroup.setDescription(groupData.get("description"));
                }
                if (groupData.containsKey("cover_picture")) {
                    existingGroup.setCover_picture(groupData.get("cover_picture"));
                }
                return ResponseEntity.ok(groupRepository.save(existingGroup));
            } else {
                return ResponseEntity.notFound().build();
            }
        }else{
            return null;
        }
    }

    // Delete a group
    @DeleteMapping("/{group_id}")
    public ResponseEntity<Void> deleteGroup(@RequestHeader("Authorization")String token,@PathVariable("group_id") int groupId) {
        if(authService.isLoggedIn(token)){
            Optional<Group> group = groupRepository.findById(groupId);
            if (group.isPresent()) {
                groupRepository.deleteById(groupId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }else{
            return null;
        }
    }

    // Add a post to a group
    @PostMapping("/{group_id}/{user_id}")
    public Post addPostToGroup(@RequestHeader("Authorization")String token,@PathVariable("group_id") int groupId, @PathVariable("user_id") int userId,
            @RequestBody Post newPost) {
        if(authService.isLoggedIn(token)){
            Optional<Group> group = groupRepository.findById(groupId);
            Optional<User> user = userRepository.findById(userId);

            if (group.isPresent() && user.isPresent()) {
                newPost.setUser(user.get());
                newPost.setGroup(group.get());
                newPost.setCreation_date(new Date()); // Ustaw datę utworzenia
                return postRepository.save(newPost);
            } else {
                throw new GroupNotFoundException("Group with id " + groupId + " or User with id " + userId + " not found");
            }
        }else{
            return null;
        }
    }

    //dodanie użytkownika do grupy
    @PostMapping("/add_user/{group_id}/{user_id}")
    public Group addUserToGroup(@RequestHeader("Authorization")String token,@PathVariable("user_id")int userId,@PathVariable("group_id")int groupId){
        if(authService.isLoggedIn(token)){
            Optional<Group> group = groupRepository.findById(groupId);
            Optional<User> user = userRepository.findById(userId);

            if (group.isPresent() && user.isPresent()) {

                Set<User> members = new HashSet<>();
                members = group.get().getMembers();
                members.add(user.get());

                group.get().setMembers(members);

                Group group_1 = group.get();
                return groupRepository.save(group_1);
            } else {
                throw new GroupNotFoundException("Group with id " + groupId + " or User with id " + userId + " not found");
            }
        }else{
            return null;
        }
    }


    // Custom exceptions for handling not found cases
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class GroupNotFoundException extends RuntimeException {
        public GroupNotFoundException(String message) {
            super(message);
        }
    }

}
