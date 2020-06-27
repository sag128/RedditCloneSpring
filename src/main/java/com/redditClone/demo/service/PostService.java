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
import java.util.stream.Stream;

import static java.util.List.*;

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
    public  Object save (PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName());

        if (subreddit != null) {
            List<Post> posts = postRepository.findAllBySubreddit(subreddit);
            log.info(posts.toString());
           List duplicate = posts.stream().filter(pn->pn.getPostName().toLowerCase().equalsIgnoreCase(postRequest.getPostName().toLowerCase())).map(postName->postName.getPostName()).collect(Collectors.toList());
            if(duplicate.size()>=1)
            {
                log.info("Post with similar name already exists in subreddit "+subreddit.getName());
                return "Post with similar name already exists in subreddit "+subreddit.getName();
            }
            if(duplicate.size()==0)
            {
                postRepository.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));
            }
        }
        if(subreddit==null)
        {
            log.info("Subreddit with name "+postRequest.getSubredditName()+" does not exit");
            return "Subreddit with name "+postRequest.getSubredditName()+" does not exit";
        }

        return "Post updated";
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
