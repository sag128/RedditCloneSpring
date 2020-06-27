package com.redditClone.demo.service;


import com.redditClone.demo.dto.SubredditDto;
import com.redditClone.demo.exception.SpringRedditException;
import com.redditClone.demo.mapper.SubredditMapper;
import com.redditClone.demo.model.Subreddit;
import com.redditClone.demo.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private  final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;
    private  final  AuthService authService;

    @Transactional
    public Object save(SubredditDto subredditDto) {

        Subreddit find = subredditRepository.findByName(subredditDto.getName());

        if (find == null) {
            log.info("Null");
            Subreddit save = subredditRepository.save((subredditMapper.mapDtoToSubreddit(subredditDto, authService.getCurrentUser())));
            subredditDto.setId(save.getId());
            return subredditDto;
        }
        else
        {
            log.info(find.toString());
            return "Subreddit with this name already exists";
        }
    }



    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {

        return subredditRepository.findAll()
                            .stream()
                            .map(subredditMapper::mapSubredditToDto)
                            .collect(Collectors.toList());


    }


    public SubredditDto getSubreddit(Long id) {

        Subreddit subreddit = subredditRepository.findById(id)
                                                .orElseThrow(()-> new SpringRedditException("No Subreddit found with id "+id));

        return subredditMapper.mapSubredditToDto(subreddit);


    }
}
