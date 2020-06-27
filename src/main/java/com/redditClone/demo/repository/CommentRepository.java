package com.redditClone.demo.repository;

import com.redditClone.demo.model.Post;
import com.redditClone.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redditClone.demo.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

    List<Comment> findByPost(Post post);

    List<Comment> findByUser(User currentUser);
}
