package com.redditClone.demo.model;

import com.redditClone.demo.exception.SpringRedditException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;


@Slf4j
public enum VoteType {
    UPVOTE(1), DOWNVOTE(-1),
    ;

    private Integer direction;

    VoteType(int direction) {
    }

    public static VoteType lookup(Integer direction) {
        log.info("In lookup where direction is "+direction);
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection().equals(direction))
                .findAny()
                .orElseThrow(() -> new SpringRedditException("Vote not found"));
    }

    public Integer getDirection() {
        log.info("In getDirection where direction is "+direction);
        return direction;

    }


}
