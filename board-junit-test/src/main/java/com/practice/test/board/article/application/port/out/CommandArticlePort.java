package com.practice.test.board.article.application.port.out;

import com.practice.test.board.article.domain.Article;

public interface CommandArticlePort {

    Article createArticle(Article article);

    Article modifyArticle(Article article);

    void deleteArticle(Long articleId);

}
