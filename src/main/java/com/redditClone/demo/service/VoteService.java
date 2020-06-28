package com.redditClone.demo.service;


import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.redditClone.demo.dto.VoteDto;
import com.redditClone.demo.dto.VoteUserDto;
import com.redditClone.demo.exception.SpringRedditException;
import com.redditClone.demo.model.Post;
import com.redditClone.demo.model.User;
import com.redditClone.demo.model.Vote;
import com.redditClone.demo.model.VoteType;
import com.redditClone.demo.repository.PostRepository;
import com.redditClone.demo.repository.UserRepository;
import com.redditClone.demo.repository.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class VoteService {

    private final PostRepository postRepository;
    private  final AuthService authService;
    private  final VoteRepository voteRepository;
    private  final UserRepository userRepository;
    @Transactional
    public void save(VoteDto voteDto) {
        int notFirst;
        Post post = postRepository.findById(voteDto.getPostId()).orElseThrow(()-> new SpringRedditException("No post with Post id "+voteDto.getPostId()));

        Optional<Vote> voteByPostUser = voteRepository.findTopByPostAndUserOrderByVotedDateTimeDesc(post,authService.getCurrentUser());
        // user can upvote or downvote once


        if(voteByPostUser.toString().equals("Optional.empty"))
        {
            notFirst=1;
        }
        else
        {
            notFirst=2;
        }

        if(voteByPostUser.isPresent() && voteByPostUser.get().getVoteType().equals(voteDto.getVoteType()))
        {
            throw new SpringRedditException("You have already "+voteDto.getVoteType()+"D for this post");
        }


        //setting post votes if upvoted or downvoted
        if(VoteType.UPVOTE.equals(voteDto.getVoteType()))
        {
            post.setVoteCount(post.getVoteCount() + notFirst);
        }
        else
        {
            post.setVoteCount(post.getVoteCount() - notFirst);
        }

        voteRepository.save(maptToVote(voteDto,post));

        postRepository.save(post);
    }

    private Vote maptToVote(VoteDto voteDto,Post post) {
        return Vote.builder()
                    .voteType(voteDto.getVoteType())
                    .post(post)
                    .user(authService.getCurrentUser())
                    .votedDateTime(Instant.now())
                    .build();

    }

    private VoteUserDto maptToVoteDto(Vote vote) {
        return VoteUserDto.builder()
                .voteType(vote.getVoteType())
                .postId(vote.getPost().getPostId())
                .postName(vote.getPost().getPostName())
                .voteTime(TimeAgo.using(vote.getVotedDateTime().toEpochMilli()))
                .build();

    }



    public List<VoteUserDto> getVoteByUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new SpringRedditException("No user with username "+username));
        List<Vote> vote = voteRepository.findByUser(user);
        return vote.stream().map(this::maptToVoteDto).collect(Collectors.toList());



    }
}
