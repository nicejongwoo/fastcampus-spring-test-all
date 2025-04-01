package com.practice.test.board.article.application.port.in;

import com.practice.test.board.article.domain.Article;

import java.util.List;

public interface GetArticleUseCase {

    Article getArticleById(Long articleId);

    List<Article> getArticlesByBoard(Long boardId);

}
