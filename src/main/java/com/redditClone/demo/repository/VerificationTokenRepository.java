package com.redditClone.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redditClone.demo.model.VerificationToken;

import java.util.Optional;


@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{

    public Optional<VerificationToken> findByToken(String token);
}
