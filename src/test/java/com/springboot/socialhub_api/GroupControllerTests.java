package com.springboot.socialhub_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.socialhub_api.api.config.FileUploadProperties;
import com.springboot.socialhub_api.api.controller.GroupController;
import com.springboot.socialhub_api.api.controller.GroupController.GroupNotFoundException;
import com.springboot.socialhub_api.api.controller.GroupController.UserNotFoundException;
import com.springboot.socialhub_api.api.model.Group;
import com.springboot.socialhub_api.api.model.Post;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

        // Zastrzeżenie zachowania fileUploadProperties.getPath()
        when(fileUploadProperties.getPath()).thenReturn("/ścieżka/do/zapisu/plików/");

        ResponseEntity<?> response = groupController.uploadCover("fake_token", 1, file);

        response.getStatusCode();
    }

    /*
     * Dodawanie postów do grupy
     * 
     */

    @Test
    void testAddPostToGroupSuccess() {
        // Przygotowanie danych testowych
        int groupId = 1;
        int userId = 2;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";
        Post newPost = new Post();
        // Ustaw dane nowego postu

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(new Group())); // Możesz dostosować odpowiednie
                                                                                      // dane grupy
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User())); // Możesz dostosować odpowiednie dane
                                                                                   // użytkownika
        when(postRepository.save(newPost)).thenReturn(newPost);

        Post createdPost = groupController.addPostToGroup(token, groupId, userId, newPost);

        assertNotNull(createdPost);
        // Możesz dodać więcej asercji, aby sprawdzić, czy utworzony post ma oczekiwane
        // wartości.
    }

    @Test
    void testAddPostToNonExistentGroup() {
        // Przygotowanie danych testowych
        int groupId = 100;
        int userId = 2;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";
        Post newPost = new Post();
        // Ustaw dane nowego postu

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> {
            groupController.addPostToGroup(token, groupId, userId, newPost);
        });
    }

    @Test
    void testAddPostWithNonExistentUserInGroup() {
        // Przygotowanie danych testowych
        int groupId = 1;
        int userId = 100; // Nieistniejący użytkownik
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";
        Post newPost = new Post();
        // Ustaw dane nowego postu

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(new Group()));
        // Możesz dostosować odpowiednie dane grupy
        when(userRepository.findById(userId)).thenReturn(Optional.empty()); // Użytkownik nie istnieje

        assertThrows(GroupNotFoundException.class, () -> {
            groupController.addPostToGroup(token, groupId, userId, newPost);
        });
    }

    @Test
    void testAddPostWithMissingContent() {
        // Przygotowanie danych testowych
        int groupId = 1;
        int userId = 2;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";
        Post newPost = new Post();

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(new Group()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        // Rzuć wyjątek RuntimeException, aby reprezentować brakującą zawartość posta
        when(groupController.addPostToGroup(token, groupId, userId, newPost))
                .thenThrow(new RuntimeException("Missing content"));

        assertThrows(RuntimeException.class, () -> {
            groupController.addPostToGroup(token, groupId, userId, newPost);
        });
    }

    /*
     * 
     * Dodawanie użytkownika do grupy
     * 
     */

    @Test
    void testAddUserToGroupSuccessfully() {
        int groupId = 1;
        int userId = 1;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";

        // Przygotowanie danych testowych
        Group group = new Group();
        group.setId(groupId);

        User user = new User();
        user.setId(userId);

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.save(any(Group.class))).thenReturn(group); // Dodajemy mockowanie zapisu grupy

        // Właściwe wywołanie metody
        Group resultGroup = groupController.addUserToGroup(token, groupId, userId);

        // Sprawdzenie, czy operacja zakończyła się sukcesem
        assertNotNull(resultGroup);

        // Sprawdzenie, czy użytkownik został dodany do grupy
        assertTrue(resultGroup.getMembers().contains(user));

        // Dodatkowo, można sprawdzić, czy grupa została zaktualizowana poprzez zapis
        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void testAddUserToNonExistentGroup() {
        // Przygotowanie danych testowych
        int groupId = 100; // Nieistniejąca grupa
        int userId = 2;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty()); // Grupa nie istnieje
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(GroupNotFoundException.class, () -> {
            groupController.addUserToGroup(token, userId, groupId);
        });
    }

    /*
     * 
     * Aktualizacja grupy
     * 
     */
    @Test
    void testUpdateGroupSuccessfully() {
        int groupId = 1;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";
        Group existingGroup = new Group();
        existingGroup.setId(groupId);

        Map<String, String> groupData = new HashMap<>();
        groupData.put("name", "Updated Group Name");
        groupData.put("description", "Updated Group Description");

        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(existingGroup));
        when(groupRepository.save(any(Group.class))).thenReturn(existingGroup);

        ResponseEntity<Group> response = groupController.updateGroup(token, groupId, groupData);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Group updatedGroup = response.getBody();
        assertNotNull(updatedGroup); // Sprawdzenie czy nie jest null
        assertEquals(groupData.get("name"), updatedGroup.getName());
        assertEquals(groupData.get("description"), updatedGroup.getDescription());
    }

    @Test
    void testUpdateGroupUnauthorized() {
        // Przygotowanie danych testowych
        int groupId = 1;
        String token = "invalid-token";

        // Dane do aktualizacji
        Map<String, String> groupData = new HashMap<>();
        groupData.put("name", "Updated Group Name");

        // Ustalenie zachowań mocków
        when(authService.isLoggedIn(token)).thenReturn(false);

        // Właściwe wywołanie metody
        ResponseEntity<Group> response = groupController.updateGroup(token, groupId, groupData);

        assertNull(response);
    }

    @Test
    void testUpdateGroupGroupNotFound() {
        // Przygotowanie danych testowych
        int groupId = 1;
        String token = "3b20d688-e8e8-49e7-86bf-aa54dbf5f7f3";

        // Dane do aktualizacji
        Map<String, String> groupData = new HashMap<>();
        groupData.put("name", "Updated Group Name");

        // Ustalenie zachowań mocków
        when(authService.isLoggedIn(token)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // Właściwe wywołanie metody
        ResponseEntity<Group> response = groupController.updateGroup(token, groupId, groupData);

        // Sprawdzenie, czy operacja zakończyła się niepowodzeniem z powodu braku
        // znalezienia grupy
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
