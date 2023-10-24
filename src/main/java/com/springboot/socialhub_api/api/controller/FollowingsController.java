package com.springboot.socialhub_api.api.controller;


import com.springboot.socialhub_api.api.model.Followings;
import com.springboot.socialhub_api.api.repositories.FollowingsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/followings")
public class FollowingsController {

    private final FollowingsRepository repository;

    public FollowingsController(FollowingsRepository repository) {
        this.repository = repository;
    }

    //get the following list for the user
    @GetMapping("/{user_id}")
    public List<Followings> select_all_followings(@PathVariable("user_id")int id){
        return repository.findByUserId(id);
    }

    //following the another user


    //unfollowing the another user

}
