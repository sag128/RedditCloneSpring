package com.redditClone.demo.service;


import com.redditClone.demo.dto.SubredditDto;
import com.redditClone.demo.dto.SubredditUpdateDto;
import com.redditClone.demo.exception.SpringRedditException;
import com.redditClone.demo.mapper.SubredditMapper;
import com.redditClone.demo.model.Subreddit;
import com.redditClone.demo.model.User;
import com.redditClone.demo.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public String updateSubreddit(Long id, SubredditUpdateDto subredditUpdateDto)
    {
        Subreddit subreddit = subredditRepository.findById(id).orElseThrow(()-> new SpringRedditException("No subreddit found with id "+id));
        User user = authService.getCurrentUser();
        Boolean login = user.getUserId().equals(subreddit.getUser().getUserId());
        if(login)
        {
            if (subredditRepository.findByName(subredditUpdateDto.getSubredditName()) == null)
            // in front end disable the save button if the user deletes and enters the same name

            {
                if (!subreddit.getName().toLowerCase().equalsIgnoreCase(subredditUpdateDto.getSubredditName().toLowerCase())) {
                    subreddit.setName(subredditUpdateDto.getSubredditName());
                    subredditRepository.save(subreddit);
                }

                if (!subreddit.getDescription().toLowerCase().equalsIgnoreCase(subredditUpdateDto.getDescription().toLowerCase())) {
                    subreddit.setDescription(subredditUpdateDto.getDescription());
                    subredditRepository.save(subreddit);
                }
            }
            else
            {
                return "Subreddit with name "+subredditUpdateDto.getSubredditName()+" exists";

            }
        }
        else
        {
            return "Wrong user";
        }


        return "Subreddit Updated";
    }
}
