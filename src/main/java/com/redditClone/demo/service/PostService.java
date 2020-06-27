package com.redditClone.demo.service;


import com.redditClone.demo.dto.PostRequest;
import com.redditClone.demo.dto.PostResponse;
import com.redditClone.demo.exception.SpringRedditException;
import com.redditClone.demo.mapper.PostMapper;
import com.redditClone.demo.model.Post;
import com.redditClone.demo.model.Subreddit;
import com.redditClone.demo.model.User;
import com.redditClone.demo.repository.PostRepository;
import com.redditClone.demo.repository.SubredditRepository;
import com.redditClone.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private  final PostRepository postRepository;
    private final PostMapper postMapper;
    private  final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;


    @Transactional
    public  void save (PostRequest postRequest)
    {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SpringRedditException("SUbreddit not found with name "+postRequest.getSubredditName()));

        postRepository.save(postMapper.map(postRequest,subreddit,authService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public PostResponse getById(Long id)
    {
        Post post = postRepository.findById(id).orElseThrow(()-> new SpringRedditException("No post with id "+id));
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts()
    {
        return postRepository.findAll().stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsBySubreddit(Long subredditId)
    {
        Subreddit subreddit = subredditRepository.findById(subredditId).orElseThrow(()->new SpringRedditException("No subreddit with id in getallpostsbysubreddit method "+subredditId));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        log.info(String.valueOf(posts.size()));

        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());


    }

    public Integer countPostsBySubreddit(Long subredditId)
    {

        Subreddit subreddit = subredditRepository.findById(subredditId).orElseThrow(()->new SpringRedditException("No subreddit with id in getallpostsbysubreddit method "+subredditId));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.size();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPostsByUsername(String username)
    {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new SpringRedditException("No username with name "+username));
        List<Post> posts = postRepository.findAllByUser(user);

        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());


    }

}
