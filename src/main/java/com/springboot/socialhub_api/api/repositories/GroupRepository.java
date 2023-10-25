package com.springboot.socialhub_api.api.repositories;

import com.springboot.socialhub_api.api.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group,Integer> {
}
