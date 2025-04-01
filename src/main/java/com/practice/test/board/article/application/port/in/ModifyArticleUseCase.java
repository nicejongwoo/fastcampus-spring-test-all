package com.practice.test.board.article.application.port.in;

import com.practice.test.board.article.adapter.in.api.dto.ArticleDto;
import com.practice.test.board.article.domain.Article;

public interface ModifyArticleUseCase {

    Article modifyArticle(ArticleDto.UpdateArticleRequest request);

}
