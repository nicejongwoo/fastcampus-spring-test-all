package com.practice.test.board.article.application.service;

import com.practice.test.board.article.adapter.in.api.dto.ArticleDto;
import com.practice.test.board.article.adapter.in.api.dto.BoardDto;
import com.practice.test.board.article.application.port.out.CommandArticlePort;
import com.practice.test.board.article.application.port.out.LoadArticlePort;
import com.practice.test.board.article.application.port.out.LoadBoardPort;
import com.practice.test.board.article.domain.*;
import com.practice.test.board.common.exception.AccessDeniedException;
import com.practice.test.board.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

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

    @Nested
    @DisplayName("Article 한 개 조회")
    class GetArticle {
        @Test
        @DisplayName("articleId 로 조회시 Article 반환")
        void return_Article() {
            var article = ArticleFixtures.article();
            given(loadArticlePort.findArticleById(any()))
                    .willReturn(Optional.of(article));

            Article result = sut.getArticleById(1L);

            then(result)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", article.getId())
                    .hasFieldOrPropertyWithValue("board.id", article.getBoard().getId())
                    .hasFieldOrPropertyWithValue("subject", article.getSubject())
                    .hasFieldOrPropertyWithValue("content", article.getContent())
                    .hasFieldOrPropertyWithValue("username", article.getUsername())
                    .hasFieldOrPropertyWithValue("createdAt", article.getCreatedAt());
        }

        @Test
        @DisplayName("존재하지 않을경우 ResourceNotFoundException throw")
        void throw_ResourceNotFoundException() {
            given(loadArticlePort.findArticleById(any()))
                    .willReturn(Optional.empty());

            thenThrownBy(() -> sut.getArticleById(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("id: 1 게시물이 없습니다.");
        }
    }

    @Test
    @DisplayName("Board의 Article 목록 조회")
    void getArticlesByBoard_listArticles() {
        var article1 = ArticleFixtures.article(1L);
        var article2 = ArticleFixtures.article(2L);
        BDDMockito.given(loadArticlePort.findArticlesByBoardId(any()))
                .willReturn(List.of(article1, article2));

        var result = sut.getArticlesByBoard(5L);

        then(result)
                .hasSize(2)
                .extracting("board.id").containsOnly(5L);
    }

    @Nested
    @DisplayName("Article 생성")
    class PostArticle {
        @Test
        @DisplayName("생성된 Article 반환")
        void return_CreatedArticleId() {
            var request = new ArticleDto.CreateArticleRequest(5L, "subject", "content", "user");

            var board = BoardFixtures.board();
            BDDMockito.given(loadBoardPort.findBoardById(any()))
                    .willReturn(Optional.of(board));

            var createdArticle = ArticleFixtures.article();
            BDDMockito.given(commandArticlePort.createArticle(any()))
                    .willReturn(createdArticle);

            var result = sut.createArticle(request);

            then(result)
                    .isEqualTo(createdArticle);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("invalidParameters")
        @DisplayName("정상적이지 않은 param이면 IllegalArgumentException")
        void throw_IllegalArgumentException(String name, String subject, String content, String username) {
            var request = new ArticleDto.CreateArticleRequest(5L, subject, content, username);
            thenThrownBy(() -> sut.createArticle(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        static Stream<Arguments> invalidParameters() {
            return Stream.of(
                    Arguments.of("subject null", null, "content", "user"),
                    Arguments.of("subject empty", "", "content", "user"),
                    Arguments.of("content null", "subject", null, "user"),
                    Arguments.of("subject empty", "subject", "", "user"),
                    Arguments.of("username null", "subject", "", null)
            );
        }
    }

    @Nested
    @DisplayName("Article 변경")
    class ModifyArticle {
        private ArticleDto.UpdateArticleRequest request;

        @BeforeEach
        void setUp() {
            request = new ArticleDto.UpdateArticleRequest(6L,
                    new BoardDto(6L, "board"),
                    "new subject",
                    "new content",
                    "user");

        }

        @Test
        @DisplayName("변경된 Article 반환")
        void return_ModifiedArticleId (){
            var article = ArticleFixtures.article();
            var board = new Board(6L, "board", BoardType.GENERAL);

            BDDMockito.given(loadArticlePort.findArticleById(any()))
                    .willReturn(Optional.of(article));
            var modifiedArticle = Article.builder()
                    .id(article.getId())
                    .board(board)
                    .subject("new subject")
                    .content("new content")
                    .username(article.getUsername())
                    .createdAt(article.getCreatedAt())
                    .build();
            BDDMockito.given(commandArticlePort.modifyArticle(any()))
                    .willReturn(modifiedArticle);

            var result = sut.modifyArticle(request);

            then(result)
                    .isEqualTo(modifiedArticle);
        }

        @Test
        @DisplayName("존재하지 않는 Article 이면 NoSuchElementException throw")
        void noExistArticle_throw_NoSuchElementException() {
            given(loadArticlePort.findArticleById(any()))
                    .willReturn(Optional.empty());

            thenThrownBy(() -> sut.modifyArticle(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("user가 다르면 AccessDeniedException throw")
        void otherUser_throw_Exception() {
            var request = new ArticleDto.UpdateArticleRequest(6L,
                    new BoardDto(6L, "board"),
                    "new subject",
                    "new content",
                    "other user");

            var article = ArticleFixtures.article();

            given(loadArticlePort.findArticleById(any()))
                    .willReturn(Optional.of(article));

            thenThrownBy(() -> sut.modifyArticle(request))
                    .isInstanceOf(AccessDeniedException.class);
        }

    }

    @Test
    @DisplayName("delete Article")
    void delete_Article() {
        willDoNothing().given(commandArticlePort).deleteArticle(any());

        sut.deleteArticle(1L);

        verify(commandArticlePort).deleteArticle(any());
    }
}
