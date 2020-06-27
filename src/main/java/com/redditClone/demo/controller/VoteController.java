package com.redditClone.demo.controller;


import com.redditClone.demo.dto.VoteDto;
import com.redditClone.demo.dto.VoteUserDto;
import com.redditClone.demo.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
public class VoteController {

    private  final VoteService voteService;

    @PostMapping
    public ResponseEntity<Void> saveVote(@RequestBody VoteDto voteDto)
    {
        voteService.save(voteDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/getVoteByUser/{username}")
    public List<VoteUserDto> getVoteByUsername(@PathVariable  String username)
    {
        return voteService.getVoteByUser(username);
    }


}
