package com.springboot.socialhub_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.socialhub_api.api.config.FileUploadProperties;
import com.springboot.socialhub_api.api.controller.GroupController;
import com.springboot.socialhub_api.api.model.Group;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.GroupRepository;
import com.springboot.socialhub_api.api.repositories.PostRepository;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import com.springboot.socialhub_api.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupControllerTests {

    @InjectMocks
    private GroupController groupController;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthService authService;

    @Mock
    private FileUploadProperties fileUploadProperties;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    void testGetGroup() throws Exception {
        int groupId = 1;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";
        Group group = new Group();
        group.setId(groupId);

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        mockMvc.perform(get("/api/group/{group_id}", groupId)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());
    }

    @Test
    void testGetGroupNotFound() throws Exception {
        int groupId = 100;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/group/{group_id}", groupId)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateGroup() throws Exception {
        int userId = 1;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";
        Map<String, String> groupData = new HashMap<>();
        groupData.put("name", "Test Group");
        groupData.put("description", "Test Group Description");
        groupData.put("coverPicture", "test.jpg");

        User user = new User();
        user.setId(userId);

        Group newGroup = new Group();
        newGroup.setName(groupData.get("name"));
        newGroup.setDescription(groupData.get("description"));
        newGroup.setCoverPicture(groupData.get("coverPicture"));
        newGroup.setOwner_id(userId);
        newGroup.getMembers().add(user);

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.save(newGroup)).thenReturn(newGroup);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonGroupData = objectMapper.writeValueAsString(groupData);

        mockMvc.perform(post("/api/group/{user_id}", userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonGroupData))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateGroupUserNotFound() throws Exception {
        int userId = 1;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";
        Map<String, String> groupData = new HashMap<>();
        groupData.put("name", "Test Group");
        groupData.put("description", "Test Group Description");
        groupData.put("coverPicture", "test.jpg");

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonGroupData = objectMapper.writeValueAsString(groupData);

        mockMvc.perform(post("/api/group/{user_id}", userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonGroupData))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUploadCover() {
        MockitoAnnotations.openMocks(this); // Inicjalizacja mocków

        // Przygotowanie danych testowych
        when(authService.isLoggedIn(anyString())).thenReturn(true);
        when(groupRepository.findById(anyInt())).thenReturn(Optional.of(new Group())); // Możesz dostosować odpowiednie
                                                                                       // dane grupy

        // Przygotowanie pliku do przesłania
        MultipartFile file = new MockMultipartFile("image", "cover.jpg", "image/jpeg", "treść_pliku".getBytes());

        // Przygotowanie ścieżki dostępu z odpowiednim zachowaniem
        when(fileUploadProperties.getPath()).thenReturn("/ścieżka/do/zapisu/plików");

        ResponseEntity<?> response = groupController.uploadCover("fake_token", 1, file);

        response.getStatusCode();
    }

    @Test
    void testUploadCoverGroupNotFound() throws Exception {
        int groupId = 1;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";
        String fileName = "test.jpg";
        MockMultipartFile file = new MockMultipartFile("image", fileName, MediaType.IMAGE_JPEG_VALUE,
                "Test image content".getBytes());

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/group/cover")
                .file(file)
                .param("groupId", String.valueOf(groupId))
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUploadCoverError() {
        MockitoAnnotations.openMocks(this); // Inicjalizacja mocków

        // Przygotowanie danych testowych
        when(authService.isLoggedIn(anyString())).thenReturn(true);
        when(groupRepository.findById(anyInt())).thenReturn(Optional.of(new Group())); // Możesz dostosować odpowiednie
                                                                                       // dane grupy

        // Przygotowanie pliku do przesłania
        MultipartFile file = new MockMultipartFile("image", "cover.jpg", "image/jpeg", "treść_pliku".getBytes());

        // Zastrzeżenie zachowania fileUploadProperties.getPath() - zwraca dowolną
        // ścieżkę, np. "/ścieżka/do/zapisu/plików/"
        when(fileUploadProperties.getPath()).thenReturn("/ścieżka/do/zapisu/plików/");

        ResponseEntity<?> response = groupController.uploadCover("fake_token", 1, file);

        response.getStatusCode();
    }

}
