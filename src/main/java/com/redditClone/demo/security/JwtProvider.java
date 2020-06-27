package com.redditClone.demo.security;

import com.redditClone.demo.exception.SpringRedditException;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static io.jsonwebtoken.Jwts.*;


@Service
@Slf4j
public class JwtProvider {

    @Value("900000")
    private Long jwtExpirationTimeInMs;

    private KeyStore keyStore;
    public  String generateToken(Authentication authentication)
    {
        User principal = (User)authentication.getPrincipal();

        return builder()
                    .setSubject(principal.getUsername())
                    .signWith(getPrivateKey())
                    .setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationTimeInMs)))
                    .compact();
    }

    public  String generateTokenByUsername(String username)
    {
        return builder()
                .setSubject(username)
                .signWith(getPrivateKey())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationTimeInMs)))
                .compact();
    }


    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/keystore.jks");
            keyStore.load(resourceAsStream, "123456".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore");
        }

    }

    private PrivateKey getPrivateKey()
    {
        try {
            return (PrivateKey) keyStore.getKey("mydomain", "123456".toCharArray());
        }
        catch (KeyStoreException|NoSuchAlgorithmException|UnrecoverableKeyException e)
        {
            throw new SpringRedditException("Exception occured while  retrieving public key from keystore");
        }
    }

    public boolean validateToken(String jwt)
    {

        parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("mydomain").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occured while " +
                    "retrieving public key from keystore", e);
        }
    }


    public String getUsernameFromJwt(String token)
    {
        Claims claims = parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token)
                                .getBody();

        log.info(claims.toString()+"In claims");
        return claims.getSubject();
    }
    public Long getJwtExpirationTimeInMs()
    {
        return jwtExpirationTimeInMs;
    }

}
