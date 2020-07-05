package com.redditClone.demo.mapper;


import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.redditClone.demo.dto.CommentsDto;
import com.redditClone.demo.model.Comment;
import com.redditClone.demo.model.Post;
import com.redditClone.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CommentsMapper {



    @Mapping(target = "id" ,ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "post" , source = "post") // if not written then will use some other class see in comments of video number 6 ex. comment.post(post.getPost()) which does not exist as there is not post field of type post in Comment model. for using post of type Post model we need to write source and target even tho the source and target name are same
    @Mapping(target = "user" , source = "user") // if not written then will use some other class see in comments of video number 6 check above.
    public abstract Comment map(CommentsDto commentsDto , Post post , User user);


    @Mapping(target = "postId", expression = "java(comment.getPost().getPostId())")
    @Mapping(target = "duration", expression = "java(getDuration(comment))")
    @Mapping(target = "userName" , expression = "java(comment.getUser().getUsername())")
    public abstract CommentsDto mapToDto(Comment comment);

    String getDuration(Comment comment)
    {
        return TimeAgo.using(comment.getCreatedDate().toEpochMilli());
    }


}
