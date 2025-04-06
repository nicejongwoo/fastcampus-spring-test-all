package com.practice.test.board.article.domain;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class JunitAssertJTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(5L, "board");
    }

    @Test
    @DisplayName("AssertJ Assertion - update article")
    void updateArticle() {
        // Arrange
        var article = Article.builder()
                .id(1L)
                .board(board)
                .subject("subject")
                .content("content")
                .username("user")
                .createdAt(LocalDateTime.now())
                .build();

        // Act
        article.update("new subject", "new content");

        // Assert
        assertThat(article.getId()).isEqualTo(1L);
        assertThat(article)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("board.id", 5L)
                .hasFieldOrPropertyWithValue("subject", "new subject")
                .hasFieldOrPropertyWithValue("content", "new content")
                .hasFieldOrProperty("createdAt");
    }

    @Test
    @DisplayName("BDD Style Assertion - update article")
    void updateArticle_BDD_style() {
        // Given
        var article = Article.builder()
                .id(1L)
                .board(board)
                .subject("subject")
                .content("content")
                .username("user")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        article.update("new subject", "new content");

        // Then
        BDDAssertions.then(article)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", article.getId())
                .hasFieldOrPropertyWithValue("board.id", article.getBoard().getId())
                .hasFieldOrPropertyWithValue("subject", "new subject")
                .hasFieldOrPropertyWithValue("content", "new content")
                .hasFieldOrPropertyWithValue("createdAt", article.getCreatedAt());
    }

}
