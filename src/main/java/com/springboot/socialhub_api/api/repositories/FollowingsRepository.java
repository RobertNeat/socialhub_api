package com.springboot.socialhub_api.api.repositories;

import com.springboot.socialhub_api.api.model.Followings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowingsRepository extends JpaRepository<Followings,Integer> {
    List<Followings> findByUserId(int userId);
}
