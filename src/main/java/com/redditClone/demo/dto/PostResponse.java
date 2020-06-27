package com.redditClone.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {

    private  Long id;
    private Long subredditId;
    private String postName;
    private String url;
    private String userName;
    private  String description;
    private String subredditName;

    private Integer voteCount;
    private Integer commentCount;
    private String duration;
    private  boolean upVote;
    private boolean downVote;




}
