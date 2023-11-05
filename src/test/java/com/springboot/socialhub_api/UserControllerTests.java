package com.springboot.socialhub_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.socialhub_api.api.config.FileUploadProperties;
import com.springboot.socialhub_api.api.controller.UserController;
import com.springboot.socialhub_api.api.model.LoginCredentials;
import com.springboot.socialhub_api.api.model.LoginResponse;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import com.springboot.socialhub_api.api.service.AuthService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UserController.class)
public class UserControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @MockBean
    private FileUploadProperties fileUploadProperties;


    // Initialize mock data if needed
    @BeforeEach
    public void setUp() {

        UserRepository userRepository = new UserRepository() {
            @Override
            public Optional<User> findByEmailAndPassword(String email, String password) {
                return Optional.empty();
            }

            @Override
            public Optional<User> findByName(String profile_picture) {
                return Optional.empty();
            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends User> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
                return null;
            }

            @Override
            public void deleteAllInBatch(Iterable<User> entities) {

            }

            @Override
            public void deleteAllByIdInBatch(Iterable<Integer> integers) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public User getOne(Integer integer) {
                return null;
            }

            @Override
            public User getById(Integer integer) {
                return null;
            }

            @Override
            public User getReferenceById(Integer integer) {
                return null;
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example) {
                return null;
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
                return null;
            }

            @Override
            public <S extends User> List<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public List<User> findAll() {
                return null;
            }

            @Override
            public List<User> findAllById(Iterable<Integer> integers) {
                return null;
            }

            @Override
            public <S extends User> S save(S entity) {
                return null;
            }

            @Override
            public Optional<User> findById(Integer integer) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Integer integer) {
                return false;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Integer integer) {

            }

            @Override
            public void delete(User entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Integer> integers) {

            }

            @Override
            public void deleteAll(Iterable<? extends User> entities) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public List<User> findAll(Sort sort) {
                return null;
            }

            @Override
            public Page<User> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends User> boolean exists(Example<S> example) {
                return false;
            }

            @Override
            public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
                return null;
            }
        };

        // Define mock behavior using Mockito for userRepository and authService

        userRepository.save(
                new User(
                        "Jan",
                        "Kowalski",
                        "jan@mail.com",
                        DigestUtils.sha256Hex("kowalski"),
                        "profile1.jpg",
                        "this jan's account",
                        true,
                        new Date()));
        userRepository.save(
                new User(
                        "Paweł",
                        "Nowak",
                        "pawel@mail.com",
                        DigestUtils.sha256Hex("nowak"),
                        "profile2.jpg",
                        "this pawel's account",
                        false,
                        new Date()));
        userRepository.save(
                new User(
                        "Miachał",
                        "Mordęga",
                        "miachal@mail.com",
                        DigestUtils.sha256Hex("mordega"),
                        "profile3.jpg",
                        "this michal's account",
                        true,
                        new Date()));
    }


    @Test
    public void getAllUsersTest() throws Exception {
        Mockito.when(authService.isLoggedIn(Mockito.anyString())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/all")
                        .header("Authorization", "auth_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    public void getSpecificUserTest() throws Exception {
        Mockito.when(authService.isLoggedIn(Mockito.anyString())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/1")
                        .header("Authorization", "auth_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void createNewUserTest() throws Exception {
        // Define a user object that you want to create
        User newUser = new User(
                "John",
                "Doe",
                "john@example.com",
                DigestUtils.sha256Hex("password123"),
                "profile4.jpg",
                "This is John's account",
                true,
                new Date()
        );

        Mockito.when(authService.isLoggedIn(Mockito.anyString())).thenReturn(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String newUserJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .header("Authorization", "auth_token")
                        .content(newUserJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }


    @Test
    public void uploadPictureTest() throws Exception {
        Mockito.when(authService.isLoggedIn(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(new User()));

        MockMultipartFile file = new MockMultipartFile("profile_picture","test-image.jpg","image/jpeg","image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/picture")
                        .file(file)
                        .header("Authorization","auth_token")
                        .param("userId","1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }




    @Test
    public void testUpdate() throws Exception {
        Mockito.when(authService.isLoggedIn(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(new User()));

        User update_user = new User();
        update_user.setId(1);
        update_user.setName("Test");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUpdateUser = objectMapper.writeValueAsString(update_user);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/user")
                .header("Authorization","auth_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdateUser))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }


    @Test
    public void testDeleteUser() throws Exception{
        Mockito.when(authService.isLoggedIn(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(new User()));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/1")
                        .header("Authorization","auth_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }


}

