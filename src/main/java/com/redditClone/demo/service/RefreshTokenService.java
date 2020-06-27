package com.redditClone.demo.service;


import com.redditClone.demo.exception.SpringRedditException;
import com.redditClone.demo.model.RefreshToken;
import com.redditClone.demo.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Ref;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private  final RefreshTokenRepository refreshTokenRepository;

      public RefreshToken generateRefreshToken()
    {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCreatedDate(Instant.now());
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);

    }

    public  void validateToken(String token)
    {
        refreshTokenRepository.findByToken(token).orElseThrow(()->new SpringRedditException("Invalid refresh token"));
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }



}
