package com.practice.test.board.article.application.service;

import com.practice.test.board.article.application.port.out.CommandArticlePort;
import com.practice.test.board.article.application.port.out.LoadArticlePort;
import com.practice.test.board.article.application.port.out.LoadBoardPort;
import com.practice.test.board.article.domain.ArticleFixtures;
import com.practice.test.board.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class NestedTest {

    private ArticleService sut;

    @Mock
    private LoadArticlePort loadArticlePort;

    @Mock
    private LoadBoardPort loadBoardPort;

    @Mock
    private CommandArticlePort commandArticlePort;

    @BeforeEach
    void setUp() {
        sut = new ArticleService(loadArticlePort, loadBoardPort, commandArticlePort);
    }

    @Test
    @DisplayName("articleId 로 조회시 Article 반환")
    void return_Article() {
        // given
        var article = ArticleFixtures.article();
        given(loadArticlePort.findArticleById(Mockito.anyLong()))
                .willReturn(Optional.of(article));

        // when
        var result = sut.getArticleById(1L);

        // then
        then(result)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", article.getId())
                .hasFieldOrPropertyWithValue("board.id", article.getBoard().getId())
                .hasFieldOrPropertyWithValue("subject", article.getSubject())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("username", article.getUsername())
                .hasFieldOrProperty("createdAt")
        ;
    }

    @Test
    @DisplayName("Article 존재하지 않을 경우 ResourceNotFoundException throw")
    void throw_ResourceNotFoundException() {
        given(loadArticlePort.findArticleById(any()))
                .willReturn(Optional.empty());

        thenThrownBy(() -> sut.getArticleById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

    }

    @Nested
    @DisplayName("Article 조회")
    class GetArticle {
        @Test
        @DisplayName("articleId 로 조회시 Article 반환")
        void return_Article() {
            // given
            var article = ArticleFixtures.article();
            given(loadArticlePort.findArticleById(Mockito.anyLong()))
                    .willReturn(Optional.of(article));

            // when
            var result = sut.getArticleById(1L);

            // then
            then(result)
                    .isNotNull()
                    .hasNoNullFieldsOrProperties()
                    .hasFieldOrPropertyWithValue("id", article.getId())
                    .hasFieldOrPropertyWithValue("board.id", article.getBoard().getId())
                    .hasFieldOrPropertyWithValue("subject", article.getSubject())
                    .hasFieldOrPropertyWithValue("content", article.getContent())
                    .hasFieldOrPropertyWithValue("username", article.getUsername())
                    .hasFieldOrProperty("createdAt")
            ;
        }

        @Test
        @DisplayName("Article 존재하지 않을 경우 ResourceNotFoundException throw")
        void throw_ResourceNotFoundException() {
            given(loadArticlePort.findArticleById(any()))
                    .willReturn(Optional.empty());

            thenThrownBy(() -> sut.getArticleById(1L))
                    .isInstanceOf(ResourceNotFoundException.class);

        }
    }

}
