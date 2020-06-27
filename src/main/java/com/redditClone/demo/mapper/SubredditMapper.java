package com.redditClone.demo.mapper;


import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.redditClone.demo.dto.SubredditDto;
import com.redditClone.demo.model.Post;
import com.redditClone.demo.model.Subreddit;
import com.redditClone.demo.model.User;
import com.redditClone.demo.service.PostService;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")

public abstract class SubredditMapper {

    @Autowired
    private PostService postService;
    @Mapping(target = "numberOfPosts" , expression = "java(mapPosts(subreddit.getId()))")

    @Mapping(target = "createdDateTime", expression = "java(dateTime(subreddit))")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "username" , source = "user.username")
    public abstract  SubredditDto mapSubredditToDto (Subreddit subreddit);  // used for get methods

    Integer mapPosts(Long id)
    {
        return  postService.countPostsBySubreddit(id);
    }

    String dateTime(Subreddit subreddit)
    {
        return TimeAgo.using(subreddit.getCreatedDate().toEpochMilli());
    }

    @InheritInverseConfiguration

    @Mapping(target = "posts" , ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    public abstract Subreddit mapDtoToSubreddit(SubredditDto subredditDto, User user);  // used for post methods
}
