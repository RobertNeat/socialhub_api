package com.springboot.socialhub_api.api.repositories;

import com.springboot.socialhub_api.api.model.Comment;
import com.springboot.socialhub_api.api.model.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findAllByPostId(int psotId);

    //tutaj się będzie pisało metody do pobierania za pomocą SQL

    /*
    // Find a user by their email
    User findByEmail(String email);

    // Find users by their name or surname
    List<User> findByNameOrSurname(String name, String surname);

    // Find users who are online
    List<User> findByIsOnlineTrue();

    // You can also define more complex queries using @Query annotation
    @Query("SELECT u FROM User u WHERE u.isOnline = true AND u.name LIKE %:name%")
    List<User> findOnlineUsersWithNameContaining(@Param("name") String name);
    */
}
