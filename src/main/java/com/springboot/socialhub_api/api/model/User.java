package com.springboot.socialhub_api.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="custom_user") //the mysql cannot handle "user" table because the same table is in mysql database
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private int id;
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(name = "surname")
    private String surname;
    @NotNull
    @Column(name = "email")
    private String email;
    @NotNull
    @Column(name = "password")
    private String password;
    @Column(name = "profile_picture")
    private String profile_picture;
    @Column(name = "description")
    private String description;
    @Column(name = "isOnline")
    private boolean isOnline;
    @Column(name = "creation_date")
    private Date creation_date;

    public User(String name, String surname, String email, String password, String profile_picture, String description, boolean isOnline, Date creation_date) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.profile_picture = profile_picture;
        this.description = description;
        this.isOnline = isOnline;
        this.creation_date = creation_date;
    }


    @Getter
    @Setter
    //user have many followings
    @OneToMany(mappedBy="user")
    @JsonIgnore
    private Set<Followings> followings;

    @Getter
    @Setter
    //user have many comments
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Comment> comments;


    @Getter
    @Setter
    //user have many posts
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Post> posts;



    @Getter
    @Setter
    //users are in many groups
     /*//działa, ale wyrzuca tyle samo error'ów co drigie (krótsze podejście)
    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name="group_id"),
            inverseJoinColumns = @JoinColumn(name="user_id"))
    */
    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private Set<Group> groups = new HashSet<>();

}
