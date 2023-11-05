package com.springboot.socialhub_api.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="custom_group")//the mysql cannot handle "group" table because the same table is in mysql database
@NoArgsConstructor
@Setter
@Getter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id")
    private int id;
    @Column(name="owner_id")
    private int owner_id;
    @Column(name="name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "cover_picture")
    private String cover_picture;
    @Column(name = "creation_date")
    private Date creation_date;


    public Group(String name, String description, String cover_picture, Date creation_date) {
        this.name = name;
        this.description = description;
        this.cover_picture = cover_picture;
        this.creation_date = creation_date;
    }
    public void setCoverPicture(String coverPicture) {
        this.cover_picture = coverPicture;
    }
    
    @Setter
    @Getter
    //the group have many posts
    @OneToMany(mappedBy = "group")
    private Set<Post> posts;

    @Setter
    @Getter
    //groups have many users
    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name="group_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private Set<User> members = new HashSet<>();;

}
