package com.springboot.socialhub_api.api.controller;

import com.springboot.socialhub_api.api.model.Comment;
import com.springboot.socialhub_api.api.model.Post;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.CommentRepository;
import com.springboot.socialhub_api.api.repositories.PostRepository;
import com.springboot.socialhub_api.api.repositories.UserRepository;

import com.springboot.socialhub_api.api.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/comment")
public class CommentController {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final AuthService authService;

    public CommentController(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository,AuthService authService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    //get the comment
    @GetMapping("/{comment_id}")
    public ResponseEntity<Comment> selectComment(@RequestHeader("Authorization") String token, @PathVariable("comment_id") int id) {
        if (authService.isLoggedIn(token)) {
            Optional<Comment> comment = commentRepository.findById(id);
            if (comment.isPresent()) {
                return ResponseEntity.ok(comment.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    //get comments by post
    @GetMapping("/all/{post_id}")
    public ResponseEntity<List<Comment>> allComments(@RequestHeader("Authorization") String token, @PathVariable("post_id") int id) {
        if (authService.isLoggedIn(token)) {
            List<Comment> comments = commentRepository.findAllByPostId(id);
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    //create the comment
    @PostMapping("create_comment/{post_id}/{user_id}")
    public ResponseEntity<Comment> createComment(
            @RequestHeader("Authorization") String token,
            @PathVariable("post_id") int postId,
            @PathVariable("user_id") int userId,
            @RequestBody Comment newComment
    ) {
        if (authService.isLoggedIn(token)) {
            // Fetch the associated post and user from the database
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with ID: " + postId));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

            // Set the fetched post and user for the new comment
            newComment.setPost(post);
            newComment.setUser(user);

            // Save the new comment
            Comment savedComment = commentRepository.save(newComment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    //update the comment
    @PutMapping
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token, @RequestBody Comment updateComment) {
        if (authService.isLoggedIn(token)) {
            Optional<Comment> commentFromDatabase = commentRepository.findById(updateComment.getId());
            if (commentFromDatabase.isPresent()) {
                String description = updateComment.getDescription();
                Date creation_date = updateComment.getCreation_date();

                if (description != null) {
                    commentFromDatabase.get().setDescription(description);
                }
                if (creation_date != null) {
                    commentFromDatabase.get().setCreation_date(creation_date);
                }

                return ResponseEntity.ok(commentRepository.save(commentFromDatabase.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }

    //delete the comment
    @DeleteMapping("/{comment_id}")
    public ResponseEntity<Void> delete(@RequestHeader("Authorization") String token, @PathVariable("comment_id") int id) {
        if (authService.isLoggedIn(token)) {
            commentRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden for unauthorized access
        }
    }
}

