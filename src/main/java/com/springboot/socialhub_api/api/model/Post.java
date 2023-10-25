package com.springboot.socialhub_api.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "post")
@NoArgsConstructor
@Setter
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "post_id")
    private int id;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "creation_date")
    private Date creation_date;

    public Post(String description, String image, Date creation_date) {
        this.description = description;
        this.image = image;
        this.creation_date = creation_date;
    }

    @Setter
    @Getter
    // post is created by one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Getter
    // post have many comments
    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    @Setter
    @Getter
    // posts are in one group
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "group_id")
    private Group group;

}
