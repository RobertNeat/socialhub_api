package com.springboot.socialhub_api.api.controller;

import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.model.Comment;
import com.springboot.socialhub_api.api.model.Post;
import com.springboot.socialhub_api.api.repositories.PostRepository;
import com.springboot.socialhub_api.api.repositories.UserRepository;

import com.springboot.socialhub_api.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/post")
public class PostController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final AuthService authService;

    public PostController(PostRepository postRepository, UserRepository userRepository,AuthService authService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    //get the post
    @GetMapping("/{post_id}")
    public Optional<Post> select_post(@RequestHeader("Authorization")String token,@PathVariable("post_id") int id)
    {
        if(authService.isLoggedIn(token)){
            return postRepository.findById(id);
        }else{
            return null;
        }
    }

    //get all post for user's feed
    @GetMapping("/all/{user_id}")
    public List<Post> all_posts(@RequestHeader("Authorization")String token,@PathVariable("user_id") int id)
    {
        if(authService.isLoggedIn(token)){
            return postRepository.findAllByUserId(id);
        }else{
            return null;
        }
    }

    //create new post on user's feed
    @PostMapping("/create/{user_id}")
    public Post createPost(@RequestHeader("Authorization")String token,@PathVariable("user_id") int id,@RequestBody Post newPost) {
        if(authService.isLoggedIn(token)){
        // Fetch the user from the database by user_id
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));
        
        // Set the user for the new post
        newPost.setUser(user);
        
        // Save the new post
        return postRepository.save(newPost);
        }else{
            return null;
        }
    }

    //update the post
    @PutMapping
    public ResponseEntity<Post> update(@RequestHeader("Authorization")String token,@RequestBody Post updatePost) {
        if (authService.isLoggedIn(token)) {
            Optional<Post> postFromDatabase = postRepository.findById(updatePost.getId());
            if (postFromDatabase.isPresent()) {
                String description = updatePost.getDescription();
                String image = updatePost.getImage();
                Date creation_date = updatePost.getCreation_date();

                if (description != null) {
                    postFromDatabase.get().setDescription(description);
                }
                if (image != null) {
                    postFromDatabase.get().setImage(image);
                }
                if (creation_date != null) {
                    postFromDatabase.get().setCreation_date(creation_date);
                }

                return ResponseEntity.ok(postRepository.save(postFromDatabase.get()));
            } else {
                return ResponseEntity.notFound().build();
            }

        }
        return null;
    }

    //delete the post
    @DeleteMapping("/{post_id}")
    public void delete(@RequestHeader("Authorization")String token,@PathVariable("post_id") int id)
    {
        if(authService.isLoggedIn(token)){
            if(authService.isLoggedIn(token)) {
                postRepository.deleteById(id);
            }
        }
    }
    


}
