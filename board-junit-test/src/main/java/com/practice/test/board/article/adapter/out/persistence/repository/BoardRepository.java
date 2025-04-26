package com.practice.test.board.article.adapter.out.persistence.repository;

import com.practice.test.board.article.adapter.out.persistence.entity.BoardJpaEntity;
import com.practice.test.board.article.domain.Board;
import com.practice.test.board.article.domain.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardJpaEntity, Long> {
    List<BoardJpaEntity> findAllByBoardType(BoardType boardType);
}
