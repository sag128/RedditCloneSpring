package com.redditClone.demo.service;

import com.redditClone.demo.model.User;
import com.redditClone.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;



    @Override
    @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("in loadbyusername");
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(()-> new UsernameNotFoundException("No User with username "+username));
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),user.isEnabled(),true,true,true,getAuthorities("USER"));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {

        log.info(Collections.singletonList(new SimpleGrantedAuthority(role)).toString());
        return Collections.singletonList(new SimpleGrantedAuthority(role));

    }
}
