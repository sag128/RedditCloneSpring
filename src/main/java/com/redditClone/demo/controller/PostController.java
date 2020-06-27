package com.redditClone.demo.controller;


import com.redditClone.demo.dto.PostRequest;
import com.redditClone.demo.dto.PostResponse;
import com.redditClone.demo.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {


    private final PostService postService;


    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostRequest postRequest)
    {
        postService.save(postRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts()
    {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost( @PathVariable  Long id)
    {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getById(id));
    }

    @GetMapping("/bySubreddit/{id}")
    public ResponseEntity<List<PostResponse>> getBySubreddit( @PathVariable Long id)
    {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPostsBySubreddit(id));
    }

    @GetMapping("/byUser/{name}")
    public ResponseEntity<List<PostResponse>> getByUsername( @PathVariable  String name)
    {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPostsByUsername(name));
    }


}
