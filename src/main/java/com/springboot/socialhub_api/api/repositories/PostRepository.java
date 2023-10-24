package com.springboot.socialhub_api.api.repositories;

import com.springboot.socialhub_api.api.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Integer> {
}
