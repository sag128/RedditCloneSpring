package com.redditClone.demo.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.redditClone.demo.dto.PostRequest;
import com.redditClone.demo.dto.PostResponse;
import com.redditClone.demo.model.*;
import com.redditClone.demo.repository.CommentRepository;
import com.redditClone.demo.repository.PostRepository;
import com.redditClone.demo.repository.VoteRepository;
import com.redditClone.demo.service.AuthService;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static  com.redditClone.demo.model.VoteType.UPVOTE;
import static  com.redditClone.demo.model.VoteType.DOWNVOTE;

@Mapper(componentModel = "spring")

public abstract class PostMapper {


    @Autowired
    private   CommentRepository commentRepository;
    @Autowired
    private   VoteRepository voteRepository;
    @Autowired
    private                  AuthService authService;

    @Mapping(target = "createdDate" , expression = "java(java.time.Instant.now())")
    @Mapping(target = "subreddit" , source = "subreddit")
    @Mapping(target = "user" , source = "user")
    @Mapping(target = "description"  , source = "postRequest.description")  // description even in subreddit so it throws multiple sources error
    @Mapping(target = "voteCount", constant = "0") //newly added
    public  abstract Post map (PostRequest postRequest, Subreddit subreddit, User user);


    @Mapping(target = "id" , source = "postId")
    @Mapping(target = "postName", source = "postName")
    @Mapping(target = "description" , source = "description")
    @Mapping(target = "url" , source = "url")
    @Mapping(target = "subredditName" , source = "subreddit.name")
    @Mapping(target = "userName" , source = "user.username")
    @Mapping(target = "subredditId", source = "subreddit.id")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target ="duration" , expression = "java(getDuration(post))")
    @Mapping(target = "upVote"  ,expression = "java(isUpVoted(post))")
    @Mapping(target = "downVote"  ,expression = "java(isDownVoted(post))")
    public abstract  PostResponse mapToDto(Post post);


    Integer commentCount(Post post)
    {
        return commentRepository.findByPost(post).size();
    }

    String getDuration(Post post)
    {
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }

    boolean isUpVoted(Post post)
    {
        return checkVoteType(post,UPVOTE);
    }

    boolean isDownVoted(Post post)
    {
        return checkVoteType(post,DOWNVOTE);
    }

    private  boolean checkVoteType(Post post, VoteType voteType)
    {
        if(authService.isLoggedIn())
        {
            Optional<Vote> voteForPostByUser =
                    voteRepository.findTopByPostAndUserOrderByVotedDateTimeDesc(post,authService.getCurrentUser());
            return voteForPostByUser.filter(vote ->vote.getVoteType().equals(voteType)).isPresent();
        }
        return false;

    }

}
