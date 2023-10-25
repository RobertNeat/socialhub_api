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
@Table(name="comment")
@NoArgsConstructor
@Setter
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comment_id")
    private int id;
    @Column(name = "description")
    private String description;
    @Column(name = "creation_date")
    private Date creation_date;

    public Comment( String description, Date creation_date) {
        this.description = description;
        this.creation_date = creation_date;
    }

    @Getter
    @Setter
    //comments are created by one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @Setter
    //comments are created to one post
    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;


}
