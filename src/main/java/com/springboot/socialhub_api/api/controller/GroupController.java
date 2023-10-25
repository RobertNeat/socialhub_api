package com.springboot.socialhub_api.api.controller;

import com.springboot.socialhub_api.api.model.Group;
import com.springboot.socialhub_api.api.repositories.GroupRepository;
import com.springboot.socialhub_api.api.repositories.PostRepository;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.model.Post;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/group")
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    GroupController(GroupRepository groupRepository, UserRepository userRepository, PostRepository postRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    
    // Get a group by group_id
    @GetMapping("/{group_id}")
    public ResponseEntity<Optional<Group>> getGroup(@PathVariable("group_id") int groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isPresent()) {
            return ResponseEntity.ok(group);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Get all groups
    @GetMapping("/all")
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }


    // Create a group
    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestParam("user_id") int userId, @RequestBody Map<String, String> groupData) {
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
    }
    

   // Update a group
    @PutMapping("/{group_id}")
    public ResponseEntity<Group> updateGroup(@PathVariable("group_id") int groupId, @RequestBody Map<String, String> groupData) {
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
    }


    // Delete a group
    @DeleteMapping("/{group_id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("group_id") int groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isPresent()) {
            groupRepository.deleteById(groupId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Add a post to a group
    @PostMapping("/{group_id}/add-post")
    public Post addPostToGroup(@PathVariable("group_id") int groupId, @RequestBody Map<String, String> postData) {
        int userId = Integer.parseInt(postData.get("user_id"));
        String description = postData.get("description");
        String image = postData.get("image");

        Optional<Group> group = groupRepository.findById(groupId);
        Optional<User> user = userRepository.findById(userId);

        if (group.isPresent() && user.isPresent()) {
            Post newPost = new Post(description, image, new Date());
            newPost.setUser(user.get());
            newPost.setGroup(group.get());
            return postRepository.save(newPost);
        } else {
            throw new GroupNotFoundException("Group with id " + groupId + " or User with id " + userId + " not found");
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
