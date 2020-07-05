package com.redditClone.demo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("In here");

        try {
            String jwt = getJwtFromRequest(request);

            if (jwtProvider.validateToken(jwt) && jwt != null) {
                String username = jwtProvider.getUsernameFromJwt(jwt);


                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request,response);

            }
        }catch (Exception e)
        {
            log.error("Cannot set user auth");
        }


    }

    private String getJwtFromRequest(HttpServletRequest request) {
       String bearerToken =  request.getHeader("Authorization");

       if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {

          return bearerToken.substring(7);
       }
       return null;

    }

}
