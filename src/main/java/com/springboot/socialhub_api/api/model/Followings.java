package com.springboot.socialhub_api.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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


    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "followed_user")
    private User followed_user;

//    @Column(name = "followed_user_id")
//    private int followed_user_id;

    public Followings(User user, User followed_user) {
        this.user=user;
        this.followed_user=followed_user;
    }




}


//https://www.baeldung.com/spring-boot-hibernate
//https://www.baeldung.com/hibernate-one-to-many
//https://www.baeldung.com/jpa-many-to-many
//https://www.baeldung.com/jpa-one-to-one