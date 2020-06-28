package com.redditClone.demo.controller;


import com.redditClone.demo.dto.CommentsDto;
import com.redditClone.demo.dto.UpdateCommentDto;
import com.redditClone.demo.service.CommentsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comments")
public class CommentsController {

    private final CommentsService commentsService;


    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto)
    {
        commentsService.save(commentsDto);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }
    @GetMapping
    public ResponseEntity<List<CommentsDto>> getAllComments()
    {
        return ResponseEntity.status(HttpStatus.OK).body(commentsService.getAllComments());
    }

    @GetMapping("/getByPost/{id}")
    public ResponseEntity<List<CommentsDto>> getCommentsByPost(@PathVariable Long id)
    {
        return ResponseEntity.status(HttpStatus.OK).body(commentsService.getCommentsByPost(id));
    }

    @GetMapping("/getByCurrentUser")
    public ResponseEntity<List<CommentsDto>> getByCurrentUser()
    {
        return ResponseEntity.status(HttpStatus.OK).body(commentsService.getCommentsByCurrentUser());
    }

    @GetMapping("/getByUsername/{username}")
    public ResponseEntity<List<CommentsDto>> getByUsername(@PathVariable String username)
    {
       return ResponseEntity.status(HttpStatus.OK).body(commentsService.getByUsername(username));
    }

    @PutMapping("/updateById/{id}")
    public ResponseEntity<String> updateComment(@PathVariable Long id,@RequestBody UpdateCommentDto updateCommentDto)
    {
        return ResponseEntity.status(HttpStatus.OK).body(commentsService.updateComment(id,updateCommentDto));
    }


}
