package com.springboot.socialhub_api.api.repositories;

import com.springboot.socialhub_api.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
