package com.springboot.socialhub_api.api.repositories;

import com.springboot.socialhub_api.api.model.Followings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingsRepository extends JpaRepository<Followings,Integer> {
}
