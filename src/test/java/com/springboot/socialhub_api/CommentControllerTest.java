package com.springboot.socialhub_api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.Path;
import com.springboot.socialhub_api.api.model.*;
import com.springboot.socialhub_api.api.repositories.*;
import com.springboot.socialhub_api.api.service.AuthService;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @Test
    public void testSelectCommentAuthenticated() throws Exception {
        // Mock authenticated request and comment retrieval
        when(authService.isLoggedIn("valid_token")).thenReturn(true);
        Comment mockComment = new Comment();
        when(commentRepository.findById(1)).thenReturn(Optional.of(mockComment));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comment/1")
                        .header("Authorization", "valid_token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(mockComment.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(mockComment.getDescription()));
    }

    @Test
    public void testSelectCommentUnauthenticated() throws Exception {
        // Mock unauthenticated request
        when(authService.isLoggedIn("invalid_token")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comment/1")
                        .header("Authorization", "invalid_token"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testAllCommentsAuthenticated() throws Exception {
        // Mock authenticated request and comments retrieval
        when(authService.isLoggedIn("valid_token")).thenReturn(true);
        List<Comment> mockComments = Arrays.asList(
                new Comment(),
                new Comment()
        );
        when(commentRepository.findAllByPostId(1)).thenReturn(mockComments);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comment/all/1")
                        .header("Authorization", "valid_token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(mockComments.get(0).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value(mockComments.get(0).getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(mockComments.get(1).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].description").value(mockComments.get(1).getDescription()));
    }

    @Test
    public void testAllCommentsUnauthenticated() throws Exception {
        // Mock unauthenticated request
        when(authService.isLoggedIn("invalid_token")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comment/all/1")
                        .header("Authorization", "invalid_token"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testCreateCommentAuthenticated() throws Exception {
        // Mock authenticated request and create a comment
        when(authService.isLoggedIn("valid_token")).thenReturn(true);
        Comment newComment = new Comment();
        newComment.setId(1); // Set an ID for the Comment
        when(postRepository.findById(1)).thenReturn(Optional.of(new Post()));
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(commentRepository.save(any(Comment.class))).thenReturn(newComment); // Mock the save operation

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/create_comment/1/1")
                        .header("Authorization", "valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newComment)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(newComment.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(newComment.getDescription()));
    }

    @Test
    public void testCreateCommentUnauthenticated() throws Exception {
        // Mock unauthenticated request
        when(authService.isLoggedIn("invalid_token")).thenReturn(false);
        Comment newComment = new Comment();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/create_comment/1/1")
                        .header("Authorization", "invalid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newComment)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testUpdateCommentAuthenticated() throws Exception {
        // Mock authenticated request and update a comment
        when(authService.isLoggedIn("valid_token")).thenReturn(true);
        Comment updatedComment = new Comment();
        updatedComment.setId(1); // Set an ID for the Comment
        Comment existingComment = new Comment();
        existingComment.setId(1); // Set an ID for the existing Comment
        when(commentRepository.findById(1)).thenReturn(Optional.of(existingComment)); // Mock the retrieval of the existing Comment
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment); // Mock the save operation

        mockMvc.perform(MockMvcRequestBuilders.put("/api/comment")
                        .header("Authorization", "valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedComment)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(updatedComment.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(updatedComment.getDescription()));
    }

    @Test
    public void testUpdateCommentUnauthenticated() throws Exception {
        // Mock unauthenticated request
        when(authService.isLoggedIn("invalid_token")).thenReturn(false);
        Comment updatedComment = new Comment();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/comment")
                        .header("Authorization", "invalid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedComment)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testDeleteCommentAuthenticated() throws Exception {
        // Mock authenticated request and delete a comment
        when(authService.isLoggedIn("valid_token")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/1")
                        .header("Authorization", "valid_token"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeleteCommentUnauthenticated() throws Exception {
        // Mock unauthenticated request
        when(authService.isLoggedIn("invalid_token")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/1")
                        .header("Authorization", "invalid_token"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    // Helper method to convert object to JSON string
    private String asJsonString(Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


