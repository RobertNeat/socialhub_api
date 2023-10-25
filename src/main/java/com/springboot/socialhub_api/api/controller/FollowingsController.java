package com.springboot.socialhub_api.api.controller;


import com.springboot.socialhub_api.api.model.Followings;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.FollowingsRepository;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.nio.file.ProviderNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("api/followings")
public class FollowingsController {

    private final FollowingsRepository repository;
    private final UserRepository user_repository;

    public FollowingsController(FollowingsRepository repository,UserRepository user_repository) {
        this.repository = repository;
        this.user_repository=user_repository;
    }

    //get the following list for the user
    @GetMapping("/{user_id}")
    public List<Followings> select_all_followings(@PathVariable("user_id")int id){
        return repository.findByUserId(id);
    }

    //following the another user
    @PostMapping("/{user_id}/{followed_user_id}")
    public ResponseEntity<Followings> follow_user(@PathVariable("user_id")int user_id,@PathVariable("followed_user_id")int followed_user_id){
        /*
        try {
            //sprzwdzenie czy użytkownicy istnieją w bazie danych
            Optional<User> user_1 = user_repository.findById(user_id);
            Optional<User> user_2 = user_repository.findById(followed_user_id);
            if((user_1.get() == null) || (user_2.get()==null)){
                throw new NoSuchElementException();
            }

            //sprawdzenie czy śledzenie istnieje w bazie danych
            Optional<Followings> dbFollowing = repository.findByUserIdAndFollowedUserId(user_id,followed_user_id);
            if(dbFollowing.get() == null){  //if the user is not followed "follow user"
                Followings followings = new Followings(user_1.get(),user_2.get());
                return ResponseEntity.ok(repository.save(followings));
            }else {
                throw new ProviderNotFoundException();
            }

        }catch(NoSuchElementException e){
            return ResponseEntity.badRequest().build(); //bad request - jeżeli użytkownicy nie istnieją
        }catch(ProviderNotFoundException e){
            return ResponseEntity.notFound().build();//not found - jeżeli wpis już istnieje
        }*/

        try{
            Optional<User> user_1 = user_repository.findById(user_id);
            Optional<User> user_2 = user_repository.findById(followed_user_id);
            Followings followings = new Followings(user_1.get(),user_2.get());
            return ResponseEntity.ok(repository.save(followings));
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }

    }


    //unfollowing the another user
    @DeleteMapping("/{user_id}/{followed_user_id}")
    public ResponseEntity<Followings> unfollow_user(@PathVariable("user_id")int user_id,@PathVariable("followed_user_id")int followed_user_id){
        try{
            Optional<User> user_1 = user_repository.findById(user_id);
            Optional<User> user_2 = user_repository.findById(followed_user_id);
            Optional<Followings> following = repository.findByUserIdAndFollowedUserId(user_1.get().getId(),user_2.get().getId());
            repository.delete(following.get());
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
