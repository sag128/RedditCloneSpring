package com.redditClone.demo.service;


import com.redditClone.demo.dto.CommentsDto;
import com.redditClone.demo.exception.SpringRedditException;
import com.redditClone.demo.mapper.CommentsMapper;
import com.redditClone.demo.model.Comment;
import com.redditClone.demo.model.NotificationEmail;
import com.redditClone.demo.model.Post;
import com.redditClone.demo.model.User;
import com.redditClone.demo.repository.CommentRepository;
import com.redditClone.demo.repository.PostRepository;
import com.redditClone.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CommentsService {
    private static final String POST_URL="";
    private final CommentsMapper commentsMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentRepository commentRepository;
    private  final MailService mailService;
    private  final MailContentBuilder mailContentBuilder;

    @Transactional
    public void save(CommentsDto commentsDto)
    {
        Post post = postRepository.findById(commentsDto.getPostId()).orElseThrow(()->new SpringRedditException("No post with post id "+commentsDto.getPostId()));
        Comment comment = commentsMapper.map(commentsDto,post,authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername()+" Posted a comment on your post "+POST_URL);
        sendCommentNotification(message,post.getUser());
        log.info(post.getUser().toString());
        log.info(authService.getCurrentUser().toString());

    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername()+" Commented on your post",user.getEmail(),message));

    }

    @Transactional(readOnly = true)
    public List<CommentsDto> getAllComments()
    {
        return commentRepository.findAll().stream().map(commentsMapper::mapToDto).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<CommentsDto> getCommentsByPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(()-> new SpringRedditException("No post with post id "+id));
       return commentRepository.findByPost(post).stream().map(commentsMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentsDto> getCommentsByCurrentUser()
    {
        return commentRepository.findByUser(authService.getCurrentUser()).stream().map(commentsMapper::mapToDto).collect(Collectors.toList());
    }


    public List<CommentsDto> getByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new SpringRedditException("No username found with username "+username));
        return commentRepository.findByUser(user).stream().map(commentsMapper::mapToDto).collect(Collectors.toList());
    }
}
