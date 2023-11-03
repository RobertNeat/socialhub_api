package com.springboot.socialhub_api.api.controller;

import com.springboot.socialhub_api.api.config.FileUploadProperties;
import com.springboot.socialhub_api.api.model.Group;
import com.springboot.socialhub_api.api.repositories.GroupRepository;
import com.springboot.socialhub_api.api.repositories.PostRepository;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import com.springboot.socialhub_api.api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.model.Post;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/group")
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private final AuthService authService;
    @Autowired
    private FileUploadProperties fileUploadProperties;

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

    //upload cover to group
    @PostMapping(path="/cover",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCover(@RequestHeader("Authorization")String token,@RequestParam("groupId") int group_id ,@RequestParam("image") MultipartFile file) {
        if (authService.isLoggedIn(token)) {
            Optional<Group> group_query = groupRepository.findById(group_id);
            if(group_query.isPresent()){
                Group group = group_query.get();
                String filePath = fileUploadProperties.getPath();

                String originalFilename = file.getOriginalFilename();
                String fileExtension = StringUtils.getFilenameExtension(originalFilename);
                String randomFileName = UUID.randomUUID().toString() + "." + fileExtension;

                try {
                    file.transferTo(new File(filePath + randomFileName));
                    System.out.println(filePath + randomFileName);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                group.setCover_picture(randomFileName);
                Group saved_group = groupRepository.save(group);
                return ResponseEntity.status(HttpStatus.OK).body(saved_group);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found in DB");
            }
        }return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
    }


    //get the group cover
    @GetMapping(path="/cover/{cover_image}",produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<?> downloadCover(@PathVariable("cover_image") String cover_name){
        Optional<Group> group_query = groupRepository.findByCoverPictureName(cover_name);
        String location = fileUploadProperties.getPath();
        if(group_query.isPresent()){
            String file_path = location+group_query.get().getCover_picture();
            try{
                byte[] image = Files.readAllBytes(new File(file_path).toPath());
                //return image;
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/jpg")).body(image);
            }catch(Exception e) {
                System.out.println(e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Image reading error");
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error fetching image resource");
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
