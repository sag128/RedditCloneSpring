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
import com.sun.org.apache.xpath.internal.operations.Bool;
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
    public  String save (PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName());

        if (subreddit != null) {
            List<Post> posts = postRepository.findAllBySubreddit(subreddit);
            Integer duplicate = getSizeOfDuplicatePostsBySubreddit(posts,postRequest.getPostName());

            log.info(posts.toString());
            if(duplicate>=1)
            {
                log.info("Post with similar name already exists in subreddit "+subreddit.getName());
                return "Post with similar name already exists in subreddit "+subreddit.getName();
            }
            if(duplicate==0)
            {
                postRepository.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));
                return "Post inserted";
            }
        }
        if(subreddit==null)
        {
            log.info("Subreddit with name "+postRequest.getSubredditName()+" does not exit");
            return "Subreddit with name "+postRequest.getSubredditName()+" does not exit";
        }

        return "Post updated";
        }

        public int getSizeOfDuplicatePostsBySubreddit(List<Post> posts, String  postNameToBeChecked)
        {
            return posts.stream().filter(pn->pn.getPostName().toLowerCase().equalsIgnoreCase(postNameToBeChecked.toLowerCase())).map(postName->postName.getPostName()).collect(Collectors.toList()).size();

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

    @Transactional
    public String updatePostById(PostRequest postRequest, Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new SpringRedditException("No post with id " + id));
        User user = authService.getCurrentUser();

        Boolean login = user.getUserId().equals(post.getUser().getUserId());

        if (login) {

            if (post.getSubreddit().getName().toLowerCase().equalsIgnoreCase(postRequest.getSubredditName())) // the creater of the post cannot change the subreddit
            {

                Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName());
                if (subreddit != null) {

                    List<Post> posts = postRepository.findAllBySubreddit(subreddit);
                    Integer duplicate = getSizeOfDuplicatePostsBySubreddit(posts, postRequest.getPostName());

                    log.info(posts.toString());
                    if (duplicate >= 1) {

                        log.info("Post with similar name already exists in subreddit " + subreddit.getName());
                        return "Post with similar name already exists in subreddit " + subreddit.getName();
                    }

                    if (duplicate == 0 && login) {
                        if (!post.getPostName().toLowerCase().equalsIgnoreCase(postRequest.getPostName().toLowerCase())) {
                            // in front end disable the save button if the user deletes and enters the same name
                            post.setPostName(postRequest.getPostName());
                            postRepository.save(post);
                            log.info("Post updated");
                        }
                        if (postRequest.getUrl() != null) {
                            post.setUrl(postRequest.getUrl());
                            postRepository.save(post);
                        }
                        if (postRequest.getDescription() != null) {
                            post.setDescription(postRequest.getDescription());
                            postRepository.save(post);
                        }

                    }


                }
                if (subreddit == null) {
                    log.info("Subreddit with name " + postRequest.getSubredditName() + " does not exit");
                    return "Subreddit with name " + postRequest.getSubredditName() + " does not exit";
                }


                return "Post updated";
            }
            else {

                return "You Cannot change subreddit";
            }
        }
        else
            {
            return "Wrong user";
        }
    }



}
