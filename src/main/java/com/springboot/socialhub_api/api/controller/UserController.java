package com.springboot.socialhub_api.api.controller;


import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.UserRepository;
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


    //get the user
    @GetMapping("/{user_id}")
    public Optional<User> select_user(@PathVariable("user_id") long id){return repository.findById(id);}

    //get the list of all users
    @GetMapping("/all")//users/all
    public List<User> select_all_users(){return repository.findAll();}


    //insert new user (register)
    @PostMapping("/register")//<-- tutaj opcjonalnie ścieżka jest (odróżniana jest na podstawie metody więc nie trzeba)
    public User create(@RequestBody User newUser){
        return repository.save(newUser);
    }

    //update the user
    @PutMapping
    public ResponseEntity<User> update(@RequestBody User updateUser){
        //trzeba sprawdzić czy updateUser.id istnieje w bazie danych
        Optional<User> userFromDatabase = repository.findById((long) updateUser.getId());
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
            if(password != null){userFromDatabase.get().setPassword(password);}
            if(profile_picture != null){userFromDatabase.get().setProfile_picture(profile_picture);}
            if(description != null){userFromDatabase.get().setDescription(description);}

            return ResponseEntity.ok(repository.save(userFromDatabase.get()));
        }else{
            //return "not found"
            return ResponseEntity.notFound().build();
        }

    }

    //delete the user
    @DeleteMapping("/{user_id}")
    public void delete(@PathVariable("user_id") int id){repository.deleteById((long)id);}//jeżeli nie będzie zwracać status.ok to zrobić typ bool



    /*-----------FUNCTIONAL ROUTES*/
    //register user

    //login a user

    //delete user

    //update the user information
}


/*
* https://www.baeldung.com/sha-256-hashing-java
* ^Haszowanie haseł
* */
