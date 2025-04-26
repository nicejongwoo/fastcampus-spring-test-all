package com.practice.test.board.article.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleFixtures {

    public static Article article(Long id) {
        var board = new Board(5L, "board", BoardType.GENERAL);

        return new Article(
                id,
                board,
                "subject" + id,
                "content" + id,
                "user" + id,
                LocalDateTime.parse("2025-03-01T09:20:30").plusDays(id));
    }

    public static Article article() {
        var board = new Board(5L, "board", BoardType.GENERAL);

        return new Article(
                1L,
                board,
                "subejct",
                "content",
                "user",
                LocalDateTime.parse("2025-03-01T09:20:30"));
    }

}
