package com.springboot.socialhub_api.api.controller;

import com.springboot.socialhub_api.api.model.Comment;
import com.springboot.socialhub_api.api.model.Post;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.CommentRepository;
import com.springboot.socialhub_api.api.repositories.PostRepository;
import com.springboot.socialhub_api.api.repositories.UserRepository;

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

    public CommentController(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    //get the comment
    @GetMapping("/{comment_id}")
    public Optional<Comment> select_post(@PathVariable("comment_id") int id)
    {
        return commentRepository.findById(id);
    }

    //get comments by post
    @GetMapping("/all/{post_id}")
    public List<Comment> all_comments(@PathVariable("post_id") int id)
    {
        return commentRepository.findAllByPostId(id);
    }

    //create the comment
    @PostMapping("create_comment/{post_id}/{user_id}")
    public Comment createComment(
        @PathVariable("post_id") int postId,
        @PathVariable("user_id") int userId,
        @RequestBody Comment newComment
    ) {
        // Fetch the associated post and user from the database
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with ID: " + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

        // Set the fetched post and user for the new comment
        newComment.setPost(post);
        newComment.setUser(user);

        // Save the new comment
        return commentRepository.save(newComment);
    }

    //update the comment
    @PutMapping
    public ResponseEntity<Comment> update(@RequestBody Comment updateComment)
    {
        Optional<Comment> commentFromDatabase = commentRepository.findById(updateComment.getId());
        if(commentFromDatabase.isPresent()){
            String description = updateComment.getDescription();
            Date creation_date = updateComment.getCreation_date();

            if(description!=null){commentFromDatabase.get().setDescription(description);}
            if(creation_date!=null){commentFromDatabase.get().setCreation_date(creation_date);}

            return ResponseEntity.ok(commentRepository.save(commentFromDatabase.get()));
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    //delete the comment
    @DeleteMapping("/{comment_id}")
    public void delete(@PathVariable("comment_id") int id) {
        commentRepository.deleteById(id);
    }

    
}
