package com.springboot.socialhub_api.api.config;

import com.springboot.socialhub_api.api.model.*;
import com.springboot.socialhub_api.api.repositories.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    Uwaga: Dane są wprowadzane przez spring-boot jednokrotnie w przypadku tworzenia bazy danych
     i ponowne uruchomienie serwera na stworzonej bazie danych (i stworzonych tabel) wywoła
      stworzenia powielonej ilości rekordów (lepiej zakomentować w razie ponownego restartu)
*/

//@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);


    @Bean
    CommandLineRunner initUser(UserRepository userRepository, CommentRepository commentRepository, PostRepository postRepository, FollowingsRepository followingsRepository,GroupRepository groupRepository) {
        return args -> {
            //pierwszy blok akcji
            {
                /*
                 * Użytkownik Jan Kowalski napisał jeden post, pod którym zostawił jeden komentarz.
                 * Śledzi użytkowników o identyfikatorze równym 2 i 3
                 * Napisał też komentarze do postów użytkowników o identyfikatorze 2 i 3
                 */
                log.info("Preloading " + userRepository.save(
                        new User(
                                "Jan",
                                "Kowalski",
                                "jan@mail.com",
                                DigestUtils.sha256Hex("kowalski"),
                                "profile1.jpg",
                                "this jan's account",
                                true,
                                new Date()
                        ))
                );

                User user_1 = userRepository.findById(1).orElse(null);

                Post post_1 = new Post("First post by Jan Kowalski", "image_1.jpeg", new Date());
                post_1.setUser(user_1);
                postRepository.save(post_1);


            }
            {//drugi blok akcji
                /*
                 * Użytkownik Paweł Nowak napisał jeden post, pod którym zostawił komentarz
                 * Śledzi użytkowników o identyfikatorze równym 1 i 3
                 * Napisał też komentarze do postów użytkowników 1 i 3
                 */
                log.info("Preloading " + userRepository.save(
                        new User(
                                "Paweł",
                                "Nowak",
                                "pawel@mail.com",
                                DigestUtils.sha256Hex("nowak"),
                                "profile2.jpg",
                                "this pawel's account",
                                false,
                                new Date()
                        ))
                );

                User user_2 = userRepository.findById(2).orElse(null);

                Post post_2 = new Post("First post by Paweł Nowak", "image_2.jpeg", new Date());
                post_2.setUser(user_2);
                postRepository.save(post_2);


            }
            {//trzeci blok akcji
                /*
                 * Użytkownik Michał Mordęga napisał jeden post, pod którym zostawił komentarz
                 * Śledzi użytkowników o identyfikatorze równym 1 i 2
                 * Napisał też komentarze do postów użytkowników 1 i 2
                 */
                log.info("Preloading "+userRepository.save(
                        new User(
                                "Miachał",
                                "Mordęga",
                                "miachal@mail.com",
                                DigestUtils.sha256Hex("mordega"),
                                "profile3.jpg",
                                "this michal's account",
                                true,
                                new Date()
                        ))
                );

                User user_3 = userRepository.findById(3).orElse(null);

                Post post_3 = new Post("First post by Michał Mordęga", "image_3.jpeg", new Date());
                post_3.setUser(user_3);
                postRepository.save(post_3);


            }
            {
                User user_1 = userRepository.findById(1).orElse(null);
                User user_2 = userRepository.findById(2).orElse(null);
                User user_3 = userRepository.findById(3).orElse(null);


                Followings following_1 = new Followings(user_1,user_2);
                Followings following_2 = new Followings(user_1,user_3);

                Followings following_3 = new Followings(user_2,user_1);
                Followings following_4 = new Followings(user_2,user_3);

                Followings following_5 = new Followings(user_3,user_1);
                Followings following_6 = new Followings(user_3,user_2);


                followingsRepository.save(following_1);
                followingsRepository.save(following_2);
                followingsRepository.save(following_3);
                followingsRepository.save(following_4);
                followingsRepository.save(following_5);
                followingsRepository.save(following_6);
            }
            {
                User user_1 = userRepository.findById(1).orElse(null);
                User user_2 = userRepository.findById(2).orElse(null);
                User user_3 = userRepository.findById(3).orElse(null);

                Post post_1 = postRepository.findById(1).orElse(null);
                Post post_2 = postRepository.findById(2).orElse(null);
                Post post_3 = postRepository.findById(3).orElse(null);


                Comment comment_1_1 = new Comment("this is the comment from jan kowalski to jan kowalski", new Date());
                comment_1_1.setUser(user_1);
                comment_1_1.setPost(post_1);
                commentRepository.save(comment_1_1);

                Comment comment_1_2 = new Comment("this is the comment from paweł nowak to jan kowalki", new Date());
                comment_1_2.setUser(user_2);
                comment_1_2.setPost(post_1);
                commentRepository.save(comment_1_2);

                Comment comment_1_3= new Comment("this is the comment from michał mordęga to jan kowalski", new Date());
                comment_1_3.setUser(user_3);
                comment_1_3.setPost(post_1);
                commentRepository.save(comment_1_3);


                Comment comment_2_1 = new Comment("this is the comment from jan kowalki to paweł nowak",new Date());
                comment_2_1.setUser(user_1);
                comment_2_1.setPost(post_2);
                commentRepository.save(comment_2_1);

                Comment comment_2_2 = new Comment("this is the comment from paweł nowak to paweł nowak", new Date());
                comment_2_2.setUser(user_2);
                comment_2_2.setPost(post_2);
                commentRepository.save(comment_2_2);

                Comment comment_2_3 = new Comment("this is the comment from michał mordęga to paweł nowak", new Date());
                comment_2_3.setUser(user_3);
                comment_2_3.setPost(post_2);
                commentRepository.save(comment_2_3);


                Comment comment_3_1 = new Comment("this is the comment from jan kowalski to michal mordęga", new Date());
                comment_3_1.setUser(user_1);
                comment_3_1.setPost(post_3);
                commentRepository.save(comment_3_1);

                Comment comment_3_2 = new Comment("this is the comment from pawel nowak to michal mordęga", new Date());
                comment_3_2.setUser(user_2);
                comment_3_2.setPost(post_3);
                commentRepository.save(comment_3_2);

                Comment comment_3_3 = new Comment("this is the comment from michal mordęga to michal mordęga", new Date());
                comment_3_3.setUser(user_3);
                comment_3_3.setPost(post_3);
                commentRepository.save(comment_3_3);
            }
            {

                Group group_1 = new Group("first group","first group description","cover_1.png",new Date());
                Group group_2 = new Group("second group","second group description","cover_1.png",new Date());

                groupRepository.save(group_1);
                groupRepository.save(group_2);
            }


        };
    }
}



/*

    @Bean
    CommandLineRunner initComment(CommentRepository commentRepository){
        return args ->{
            log.info("Preloading "+commentRepository.save(
                    new Comment(
                            "this first comment description",
                            new Date()
                    ))
            );
            log.info("Preloading "+commentRepository.save(
                    new Comment(
                            "this second comment description",
                            new Date()
                    ))
            );
            log.info("Preloading "+commentRepository.save(
                    new Comment(
                            "this third comment description",
                            new Date()
                    ))
            );

        };
    }
*/