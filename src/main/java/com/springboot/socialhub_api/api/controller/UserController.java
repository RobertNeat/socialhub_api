package com.springboot.socialhub_api.api.controller;


import com.springboot.socialhub_api.api.config.FileUploadProperties;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import com.springboot.socialhub_api.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.codec.digest.DigestUtils;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import java.util.UUID;
import org.springframework.util.StringUtils;


@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserRepository repository;
    private final AuthService authService;
    @Autowired
    private FileUploadProperties fileUploadProperties;

    UserController(UserRepository repository,AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }



    //get the list of all users
    @GetMapping("/all")//users/all
    @Operation(summary = "Get all users", description = "Get all user objects based on the valid token")
    @ApiResponses(value = {
            @ApiResponse(description = "All user fetched successfully",responseCode = "200"),
            @ApiResponse(description = "Unauthorized",responseCode = "401"),
    })
    public ResponseEntity<?> select_all_users(@RequestHeader("Authorization")String token){
        if(authService.isLoggedIn(token)){
            return ResponseEntity.ok(repository.findAll());
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }

    //get the user
    @GetMapping("/{user_id}")
    @Operation(summary = "Get a user", description = "Get a user data based on user identifier and the valid token")
    @ApiResponses(value = {
            @ApiResponse(description = "User found",responseCode = "200"),
            @ApiResponse(description = "Unauthorized",responseCode = "401"),
    })
    @Parameters(
            value={
                    @Parameter(name = "user_id",example="46"),
            }
    )
    public ResponseEntity<?> select_user(@RequestHeader("Authorization")String token,@PathVariable("user_id") int id){
        if(authService.isLoggedIn(token)){
            return ResponseEntity.ok(repository.findById(id));
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }


    //insert new user (register) - stworzenie nowego użytkownika
    @Operation(summary = "Create a new user", description = "Create a user based on user object and the valid token")
    @ApiResponses(value = {
            @ApiResponse(description = "User created successfully",responseCode = "200"),
            @ApiResponse(description = "Unauthorized",responseCode = "401"),
    })
    @Parameters(
            value={
                    @Parameter(name = "name",example="Jan"),
                    @Parameter(name="surname",example="Kowalski"),
                    @Parameter(name="email",example="jan@mail.com"),
                    @Parameter(name="password",example="passw*rd"),
                    @Parameter(name="profile_picture",example="image.jpg"),
                    @Parameter(name="description",example="profile description")
            }
    )
    @PostMapping()
    public ResponseEntity<?> create(@RequestHeader("Authorization")String token, @RequestBody User newUser){
        if(authService.isLoggedIn(token)){
            String raw_password = newUser.getPassword();
            String encoded_password = DigestUtils.sha256Hex(raw_password);
            newUser.setPassword(encoded_password);
            return ResponseEntity.ok(repository.save(newUser));

        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }

    //uploading the profile_picture
    @PostMapping(path="/picture",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPicture(@RequestHeader("Authorization")String token,@RequestParam("userId") int user_id ,@RequestParam("profile_picture") MultipartFile file){
        if(authService.isLoggedIn(token)){
            Optional<User> user_query = repository.findById(user_id);
            if(user_query.isPresent()){
                User user = user_query.get();
                String filePath = fileUploadProperties.getPath();

                String originalFilename = file.getOriginalFilename();
                String fileExtension = StringUtils.getFilenameExtension(originalFilename);
                String randomFileName = UUID.randomUUID().toString() + "." + fileExtension;

                try{
                    //withpout changing the name
                    //file.transferTo(new File(filePath+file.getOriginalFilename()));
                    //with generating random name
                    file.transferTo(new File(filePath + randomFileName));

                    //System.out.println(filePath+file.getOriginalFilename());
                    System.out.println(filePath+randomFileName);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                user.setProfile_picture(randomFileName);
                User saved_user = repository.save(user);
                return ResponseEntity.status(HttpStatus.OK).body(saved_user);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found in DB");
        }return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized");
    }


    //get the profile picture
    @GetMapping(path="/picture/{profile_picture}",produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE}) //produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE}
    public ResponseEntity<?> downloadPicture(@PathVariable("profile_picture") String picture_name){
        //if(authService.isLoggedIn(token)){
            Optional<User> user_query = repository.findByName(picture_name);
            String location = fileUploadProperties.getPath();
            if(user_query.isPresent()){
                String file_path = location+user_query.get().getProfile_picture();
                try{
                    byte[] image = Files.readAllBytes(new File(file_path).toPath());
                    //return image;
                    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/jpg")).body(image);
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Image reading error");
                }
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error fetching image resource");
            }
    }


    //update the user
    @Operation(summary = "Update a user", description = "Update a user based on user object and the valid token")
    @ApiResponses(value = {
            @ApiResponse(description = "User updated successfully",responseCode = "200"),
            @ApiResponse(description = "User not found",responseCode = "404"),
            @ApiResponse(description = "Unauthorized",responseCode = "401"),
    })
    @Parameters(
            value={
                    @Parameter(name = "name",example="Jan"),
                    @Parameter(name="surname",example="Kowalski"),
                    @Parameter(name="email",example="jan@mail.com"),
                    @Parameter(name="password",example="passw*rd"),
                    @Parameter(name="profile_picture",example="image.jpg"),
                    @Parameter(name="description",example="profile description")
            }
    )
    @PutMapping
    public ResponseEntity<User> update(@RequestHeader("Authorization")String token,@RequestBody User updateUser){
        if(authService.isLoggedIn(token)){
            //trzeba sprawdzić czy updateUser.id istnieje w bazie danych
            Optional<User> userFromDatabase = repository.findById(updateUser.getId());
            if(userFromDatabase.isPresent()) {
                String name = updateUser.getName();
                String surname = updateUser.getSurname();
                String email = updateUser.getEmail();
                String password = updateUser.getPassword();
                String profile_picture = updateUser.getProfile_picture();
                String description = updateUser.getDescription();

                if(name != null){userFromDatabase.get().setName(name);}
                if(surname != null){userFromDatabase.get().setSurname(surname);}
                if(email != null){userFromDatabase.get().setEmail(email);}
                if(password != null){userFromDatabase.get().setPassword(DigestUtils.sha256Hex(password));}
                if(profile_picture != null){userFromDatabase.get().setProfile_picture(profile_picture);}
                if(description != null){userFromDatabase.get().setDescription(description);}

                return ResponseEntity.ok(repository.save(userFromDatabase.get()));
            }else{
                return ResponseEntity.notFound().build();
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    //delete the user
    @Operation(summary = "Delete a user", description = "Delete a user based on user identifier and the valid token")
    @ApiResponses(value = {
            @ApiResponse(description = "User deleted successfully",responseCode = "200"),
            @ApiResponse(description = "User not found",responseCode = "404"),
            @ApiResponse(description = "Unauthorized",responseCode = "401"),
    })
    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization")String token,@PathVariable("user_id") int id){
        if(authService.isLoggedIn(token)){
            try {
                Optional<User> delete_user = repository.findById(id);
                repository.delete(delete_user.get());
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }



}

