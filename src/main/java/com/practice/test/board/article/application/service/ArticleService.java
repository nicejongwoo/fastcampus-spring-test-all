package com.practice.test.board.article.application.service;

import com.practice.test.board.article.adapter.in.api.dto.ArticleDto;
import com.practice.test.board.article.application.port.in.CreateArticleUseCase;
import com.practice.test.board.article.application.port.in.DeleteArticleUseCase;
import com.practice.test.board.article.application.port.in.GetArticleUseCase;
import com.practice.test.board.article.application.port.in.ModifyArticleUseCase;
import com.practice.test.board.article.application.port.out.CommandArticlePort;
import com.practice.test.board.article.application.port.out.LoadArticlePort;
import com.practice.test.board.article.application.port.out.LoadBoardPort;
import com.practice.test.board.article.domain.Article;
import com.practice.test.board.common.exception.AccessDeniedException;
import com.practice.test.board.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ArticleService implements GetArticleUseCase, CreateArticleUseCase, ModifyArticleUseCase, DeleteArticleUseCase {

    private final LoadArticlePort loadArticlePort;
    private final LoadBoardPort loadBoardPort;
    private final CommandArticlePort commandArticlePort;

    public ArticleService(LoadArticlePort loadArticlePort,
                          LoadBoardPort loadBoardPort,
                          CommandArticlePort commandArticlePort) {
        this.loadArticlePort = loadArticlePort;
        this.loadBoardPort = loadBoardPort;
        this.commandArticlePort = commandArticlePort;
    }

    @Override
    @Transactional(readOnly = true)
    public Article getArticleById(Long articleId) {
        return loadArticlePort.findArticleById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("id: " + articleId + " 게시물이 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Article> getArticlesByBoard(Long boardId) {
        return loadArticlePort.findArticlesByBoardId(boardId);
    }

    @Override
    @Transactional
    public Article createArticle(ArticleDto.CreateArticleRequest request) {
        Assert.hasLength(request.subject(), "subject should not empty");
        Assert.hasLength(request.content(), "content should not empty");
        Assert.hasLength(request.username(), "username should not empty");

        var board = loadBoardPort.findBoardById(request.boardId()).orElseThrow();

        Article article = Article.builder()
                .board(board)
                .subject(request.subject())
                .content(request.content())
                .username(request.username())
                .createdAt(LocalDateTime.now())
                .build();

        return commandArticlePort.createArticle(article);
    }

    @Override
    public Article modifyArticle(ArticleDto.UpdateArticleRequest request) {
        var article = loadArticlePort.findArticleById(request.id()).orElseThrow();

        if (!article.getUsername().equals(request.username())) {
            throw new AccessDeniedException("");
        }

        article.update(request.subject(), request.content());

        return commandArticlePort.modifyArticle(article);
    }

    @Override
    public void deleteArticle(Long articleId) {
        commandArticlePort.deleteArticle(articleId);
    }
}
