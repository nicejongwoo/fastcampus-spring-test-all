package com.practice.test.board.article.adapter.out.persistence;

import com.practice.test.board.article.adapter.out.persistence.entity.ArticleJpaEntity;
import com.practice.test.board.article.adapter.out.persistence.repository.ArticleRepository;
import com.practice.test.board.article.application.port.out.CommandArticlePort;
import com.practice.test.board.article.application.port.out.LoadArticlePort;
import com.practice.test.board.article.domain.Article;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ArticlePersistenceAdapter implements LoadArticlePort, CommandArticlePort {

    private final ArticleRepository articleRepository;

    public ArticlePersistenceAdapter(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Optional<Article> findArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleJpaEntity::toDomain);
    }

    @Override
    public List<Article> findArticlesByBoardId(Long boardId) {
        return articleRepository.findByBoardId(boardId).stream()
                .map(ArticleJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Article createArticle(Article article) {
        var savedArticle = articleRepository.save(ArticleJpaEntity.fromDomain(article));
        return savedArticle.toDomain();
    }

    @Override
    public Article modifyArticle(Article article) {
        var savedArticle = articleRepository.save(ArticleJpaEntity.fromDomain(article));
        return savedArticle.toDomain();
    }

    @Override
    public void deleteArticle(Long articleId) {
        articleRepository.deleteById(articleId);
    }
}
