package com.practice.test.board.article.application.port.in;

import com.practice.test.board.article.domain.Board;
import com.practice.test.board.article.domain.BoardType;

import java.util.List;

public interface GetBoardUseCase {

    Board getBoard(Long boardId);

    List<Board> listBoardByBoardType(BoardType boardType);
}
