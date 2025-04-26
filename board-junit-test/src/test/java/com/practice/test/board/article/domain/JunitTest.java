package com.practice.test.board.article.domain;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class JunitTest {

    @BeforeAll
    static void initAll() {
        System.out.println("Before All");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("After All");
    }

    @BeforeEach
    void setUp() {
        System.out.println("Before Each");
    }

    @AfterEach
    void tearDown() {
        System.out.println("After Each");
    }

    @Test
    @DisplayName("성공 테스트 - Article 생성")
    void constructArticle() {
        // Arrange
        var board = new Board(5L, "board", BoardType.GENERAL);

        // Act
        var article = Article.builder()
                .id(1L)
                .board(board)
                .subject("subject")
                .content("content")
                .username("user")
                .createdAt(LocalDateTime.now())
                .build();

        // Assert
        assertEquals(1L, article.getId());
        assertEquals(article.getBoard(), board);
        assertEquals("subject", article.getSubject());
        assertEquals("content", article.getContent());
        assertNotEquals("subject2", article.getSubject());
        assertNotNull(article.getCreatedAt());
        System.out.println("Succeeding test");
    }

    @Test
    @DisplayName("실패 테스트")
    void failingTest() {
        assertEquals(4, 2, +2, "테스트 실패시 출력되는 failure message");
        System.out.println("Failing test");
    }

    @Test
    @Disabled("이 테스트를 Disable 하는 이유")
    void skippingTest() {
        System.out.println("Disabled test");
        assertEquals(3, 1 + 2);
    }

}
