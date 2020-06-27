package com.redditClone.demo.repository;

import com.redditClone.demo.model.Post;
import com.redditClone.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redditClone.demo.model.Vote;

import java.util.List;
import java.util.Optional;


@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{

    Optional<Vote> findTopByPostAndUserOrderByVotedDateTimeDesc(Post post, User user); // for finding the latest votetype on the post and displaying using posts api not in vote api

    List<Vote> findByUser(User user);
}
