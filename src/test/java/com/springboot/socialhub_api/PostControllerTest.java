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
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @Before
    public void setUp() {
        // Set up your mocks and behavior here
    }

    // Define the asJsonString method to convert objects to JSON strings
    private String asJsonString(Object obj) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testSelectPostAuthenticated() throws Exception {
        // Mock authentication and repository behavior
        when(authService.isLoggedIn("valid_token")).thenReturn(true);
        when(postRepository.findById(1)).thenReturn(Optional.of(new Post(/* create a Post object here */)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/1")
                        .header("Authorization", "valid_token"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSelectPostUnauthenticated() throws Exception {
        // Mock unauthenticated request
        when(authService.isLoggedIn("invalid_token")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/1")
                        .header("Authorization", "invalid_token"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testAllPostsAuthenticated() throws Exception {
        // Mock authentication and repository behavior
        when(authService.isLoggedIn("valid_token")).thenReturn(true);
        when(postRepository.findAllByUserId(1)).thenReturn(Arrays.asList(new Post(/* create Post objects here */)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/all/1")
                        .header("Authorization", "valid_token"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreatePost() throws Exception {
        // Mock authentication, repository, and user behavior
        when(authService.isLoggedIn("valid_token")).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(new User(/* create a User object here */)));

        // Use Jackson ObjectMapper to convert the object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String newPostJson = objectMapper.writeValueAsString(new Post(/* create a new Post object here */));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/create/1")
                        .header("Authorization", "valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPostJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUploadImageAuthenticated() throws Exception {
        // Mock authentication and repository behavior
        when(authService.isLoggedIn("valid_token")).thenReturn(true);
        when(postRepository.findById(1)).thenReturn(Optional.of(new Post(/* create a Post object here */)));

        // Create a mock MultipartFile to simulate file upload
        MockMultipartFile file = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "image content".getBytes());

        // Perform the POST request with the mock file
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/post/image")
                        .file(file)
                        .param("postId", "1")
                        .header("Authorization", "valid_token"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUploadImageUnauthenticated() throws Exception {
        // Mock unauthenticated request
        when(authService.isLoggedIn("invalid_token")).thenReturn(false);

        // Create a mock MultipartFile to simulate file upload
        MockMultipartFile file = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "image content".getBytes());

        // Perform the POST request with the mock file
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/post/image")
                        .file(file)
                        .param("postId", "1")
                        .header("Authorization", "invalid_token"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testUpdateAuthenticated() throws Exception {
        // Mock authentication and repository behavior
        when(authService.isLoggedIn("valid_token")).thenReturn(true);
        when(postRepository.findById(1)).thenReturn(Optional.of(new Post(/* create a Post object here */)));

        // Create a mock request body for the update
        Post updatePost = new Post(/* create a Post object with updated data */);
        updatePost.setId(1);
        // Perform the PUT request with the mock update data
        mockMvc.perform(MockMvcRequestBuilders.put("/api/post")
                        .header("Authorization", "valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatePost)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void testUpdateUnauthenticated() throws Exception {
        // Mock unauthenticated request
        when(authService.isLoggedIn("invalid_token")).thenReturn(false);

        // Create a mock request body for the update
        Post updatePost = new Post(/* create a Post object with updated data */);

        // Perform the PUT request with the mock update data
        mockMvc.perform(MockMvcRequestBuilders.put("/api/post")
                        .header("Authorization", "invalid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatePost)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    @Test
    public void testDeleteAuthenticated() throws Exception {
        // Mock authentication and repository behavior
        when(authService.isLoggedIn("valid_token")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post/1")
                        .header("Authorization", "valid_token"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteUnauthenticated() throws Exception {
        // Mock unauthenticated request
        when(authService.isLoggedIn("invalid_token")).thenReturn(false);

        // Perform the DELETE request with the unauthenticated token
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post/1")
                        .header("Authorization", "invalid_token"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}


