package com.practice.test.board.article.adapter.out.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.BDDAssertions.then;

@DataJpaTest
@Sql("/data/ArticleRepositoryFixtureTest.sql")
class ArticleRepositoryFixtureTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void listAllArticles() {
        var result = articleRepository.findByBoardId(5L);

        then(result).hasSize(2);
    }

    @Test
    @Sql("/data/ArticleRepositoryFixtureTest2.sql")
    void listAllArticles2() {
        var result = articleRepository.findByBoardId(5L);

        then(result).hasSize(3);
    }
}