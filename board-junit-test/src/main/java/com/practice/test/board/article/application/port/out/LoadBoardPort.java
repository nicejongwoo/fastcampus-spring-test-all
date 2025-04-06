package com.practice.test.board.article.application.port.out;

import com.practice.test.board.article.domain.Board;

import java.util.Optional;

public interface LoadBoardPort {

    Optional<Board> findBoardById(Long boardId);

}
