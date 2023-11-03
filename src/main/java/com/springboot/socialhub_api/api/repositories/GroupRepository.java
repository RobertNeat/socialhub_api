package com.springboot.socialhub_api.api.repositories;

import com.springboot.socialhub_api.api.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group,Integer> {


    @Query("SELECT g FROM Group g WHERE g.cover_picture = :cover_picture")
    Optional<Group> findByCoverPictureName(String cover_picture);
}
