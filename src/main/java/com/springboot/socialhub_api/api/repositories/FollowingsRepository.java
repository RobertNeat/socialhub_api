package com.springboot.socialhub_api.api.repositories;

import com.springboot.socialhub_api.api.model.Followings;
import com.springboot.socialhub_api.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowingsRepository extends JpaRepository<Followings,Integer> {
    List<Followings> findByUserId(int userId);

    @Query("SELECT f FROM Followings f WHERE f.user.id = :userId AND f.followed_user.id = :followedUserId")
    Optional<Followings> findByUserIdAndFollowedUserId(int userId, int followedUserId);//to check if the target-user is followed


}
