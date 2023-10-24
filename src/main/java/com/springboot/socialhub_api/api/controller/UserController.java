package com.springboot.socialhub_api.api.controller;


import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserRepository repository;

    UserController(UserRepository repository){
        this.repository = repository;
    }

    //get the list of all users
    @GetMapping("/all")//users/all
    public List<User> select_all_users(){return repository.findAll();}

    //get the user
    @GetMapping("/{user_id}")
    public Optional<User> select_user(@PathVariable("user_id") int id){return repository.findById(id);}


    //insert new user (register) - stworzenie nowego użytkownika
    @PostMapping()
    public User create(@RequestBody User newUser){
        String raw_password = newUser.getPassword();
        String encoded_password = DigestUtils.sha256Hex(raw_password);

        newUser.setPassword(encoded_password);

        return repository.save(newUser);
    }

    //update the user
    @PutMapping
    public ResponseEntity<User> update(@RequestBody User updateUser){
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

    }

    //delete the user
    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> delete(@PathVariable("user_id") int id){
        try {
            Optional<User> delete_user = repository.findById(id);
            repository.delete(delete_user.get());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


}

