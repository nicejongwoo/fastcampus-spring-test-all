package com.practice.test.board.article.application.service;

import com.practice.test.board.article.application.port.in.GetBoardUseCase;
import com.practice.test.board.article.application.port.out.LoadBoardPort;
import com.practice.test.board.article.domain.Board;
import com.practice.test.board.article.domain.BoardType;
import com.practice.test.board.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService implements GetBoardUseCase {

    private final LoadBoardPort boardPort;

    public BoardService(LoadBoardPort boardPort) {
        this.boardPort = boardPort;
    }

    @Override
    public Board getBoard(Long boardId) {
        return boardPort.findBoardById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException(""));
    }

    @Override
    public List<Board> listBoardByBoardType(BoardType boardType) {
        return boardPort.findBoardByBoardType(boardType);
    }
}
