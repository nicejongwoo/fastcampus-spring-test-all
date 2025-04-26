package com.practice.test.board.article.adapter.in.api;

import com.practice.test.board.article.application.port.in.GetBoardUseCase;
import com.practice.test.board.article.domain.Board;
import com.practice.test.board.article.domain.BoardType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class BoardController {

    private final GetBoardUseCase getBoardUseCase;

    public BoardController(GetBoardUseCase getBoardUseCase) {
        this.getBoardUseCase = getBoardUseCase;
    }

    @GetMapping("/boards/{boardId}")
    Board getBoard(@PathVariable("boardId") Long boardId) {
        return getBoardUseCase.getBoard(boardId);
    }

    @GetMapping(value = "/boards", params = "boardType")
    List<Board> getBoard(@RequestParam(value = "boardType") BoardType boardType) {
        return getBoardUseCase.listBoardByBoardType(boardType);
    }

}
