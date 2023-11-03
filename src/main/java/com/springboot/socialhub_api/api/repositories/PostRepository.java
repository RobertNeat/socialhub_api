package com.springboot.socialhub_api.api.repositories;

import com.springboot.socialhub_api.api.model.Post;

import java.util.List;
import java.util.Optional;

import com.springboot.socialhub_api.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post,Integer> {
    List<Post> findAllByUserId(int userId);

    @Query("SELECT p FROM Post p WHERE p.image = :image")
    Optional<Post> findByImageName(String image);
}
