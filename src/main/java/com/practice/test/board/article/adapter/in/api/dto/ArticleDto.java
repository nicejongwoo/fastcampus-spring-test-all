package com.practice.test.board.article.adapter.in.api.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ArticleDto {

    public record CreateArticleRequest(
            @NotNull
            Long boardId,
            @NotNull
            String subject,
            @NotNull
            String content,
            @NotEmpty
            String username
    ) {

    }

    public record UpdateArticleRequest(
            Long id,
            @NotNull
            BoardDto board,
            @NotNull
            String subject,
            @NotNull
            String content,
            @NotNull
            String username
    ) {

    }
}
