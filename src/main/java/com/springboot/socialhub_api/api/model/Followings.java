package com.springboot.socialhub_api.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="followings")
@NoArgsConstructor
@Getter
@Setter
public class Followings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "followings_id")
    private int id;

    @Column(name = "followed_user_id")
    private int followed_user_id;

    public Followings(int followed_user_id) {
        this.followed_user_id = followed_user_id;
    }

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}


//https://www.baeldung.com/spring-boot-hibernate
//https://www.baeldung.com/hibernate-one-to-many
//https://www.baeldung.com/jpa-many-to-many
//https://www.baeldung.com/jpa-one-to-one