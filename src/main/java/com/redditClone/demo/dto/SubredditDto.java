package com.redditClone.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class SubredditDto {
    private String name;
    private String description;
    private Integer numberOfPosts=0;
    private Long id;
    private String createdDateTime;
    private  String username;
    private Long userId;


}
