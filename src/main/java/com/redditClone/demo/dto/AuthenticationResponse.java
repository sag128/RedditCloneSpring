package com.redditClone.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AuthenticationResponse {


    private String authenticationToken;
    private String username;

    //new

    private Instant expiresAt;
    private String refreshToken;



}
