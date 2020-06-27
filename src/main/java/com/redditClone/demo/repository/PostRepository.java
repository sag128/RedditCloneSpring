package com.redditClone.demo.repository;

import com.redditClone.demo.model.Subreddit;
import com.redditClone.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.redditClone.demo.model.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{

    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findAllByUser(User user);

    Post findByPostName(String postName);


}
