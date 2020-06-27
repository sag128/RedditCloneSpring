package com.redditClone.demo.dto;


import com.redditClone.demo.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteDto {
    private Long postId;
    private VoteType voteType;
    private String voteTime;


}
