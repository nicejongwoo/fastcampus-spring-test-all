package com.practice.test.board.article.application.service;

import com.practice.test.board.article.adapter.in.api.dto.ArticleDto;
import com.practice.test.board.article.application.port.out.CommandArticlePort;
import com.practice.test.board.article.application.port.out.LoadArticlePort;
import com.practice.test.board.article.application.port.out.LoadBoardPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@ExtendWith(MockitoExtension.class)
public class ExceptionTest {

    private ArticleService sut;

    @Mock
    private LoadArticlePort loadArticlePort;

    @Mock
    private CommandArticlePort commandArticlePort;

    @Mock
    private LoadBoardPort loadBoardPort;

    @BeforeEach
    void setUp() {
        sut = new ArticleService(loadArticlePort, loadBoardPort, commandArticlePort);
    }

    @Test
    @DisplayName("subject가 정상적이지 않으면 IllegalArgumentException")
    void throwIllegalArgumentException() {
        var request = new ArticleDto.CreateArticleRequest(5L, null, "content", "user");

        thenThrownBy(() -> sut.createArticle(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("subject should not empty");
    }

}
