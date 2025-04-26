package com.practice.test.board.article.application.port.out;

import com.practice.test.board.article.domain.Board;
import com.practice.test.board.article.domain.BoardType;

import java.util.List;
import java.util.Optional;

public interface LoadBoardPort {

    Optional<Board> findBoardById(Long boardId);

    List<Board> findBoardByBoardType(BoardType boardType);

}
