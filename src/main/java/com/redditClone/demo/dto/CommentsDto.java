package com.redditClone.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentsDto {

    private Long id;
    private Long postId;
    private Instant createdDate;
    private String text;
    private String userName;
    private String duration;




}
