package com.springboot.socialhub_api.api.controller;


import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserRepository repository;

    UserController(UserRepository repository){
        this.repository = repository;
    }


    //insert new user (register)
    @PostMapping("/register")
    User newUser(@RequestBody User newUser){
        return repository.save(newUser);
    }




    /*-----------FUNCTIONAL ROUTES*/
    //register user

    //login a user

    //delete user

    //update the user information
}
