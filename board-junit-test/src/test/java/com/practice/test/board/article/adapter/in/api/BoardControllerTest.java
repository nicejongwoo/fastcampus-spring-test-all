package com.practice.test.board.article.adapter.in.api;

import com.practice.test.board.article.application.port.in.GetBoardUseCase;
import com.practice.test.board.article.domain.Board;
import com.practice.test.board.article.domain.BoardFixtures;
import com.practice.test.board.article.domain.BoardType;
import com.practice.test.board.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * - GET /boards/{boardId}
 * - GET /boards?boardType={boardType}
 */
@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetBoardUseCase getBoardUseCase;

    @Nested
    @DisplayName("GET /boards/{boardId}")
    class GetBoard {
        @Test
        @DisplayName("boardId에 해당하는 Board 정보 반환, 200 OK")
        void testGetBoardThen200OK() throws Exception {
            // stub
            var boardId = 1L;
            var board = BoardFixtures.board(boardId);
            given(getBoardUseCase.getBoard(any())).willReturn(board);

            mockMvc
                    .perform(
                            get("/boards/{boardId}", boardId)
                    )
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(boardId),
                            jsonPath("$.name").value(board.getName())
                    );

        }

        @Test
        @DisplayName("존재하지 않는 boardId에 대해 404 NOT FOUND")
        void testGetBoardThen404NotFound() throws Exception {
            // stub
            var boardId = 1L;
            given(getBoardUseCase.getBoard(any())).willThrow(new ResourceNotFoundException(""));

            mockMvc
                    .perform(
                            get("/boards/{boardId}", boardId)
                    )
                    .andExpectAll(
                            status().isNotFound()
                    );
        }
    }

    @Nested
    @DisplayName("GET /boards?boardType={boardType}")
    class ListBoardByBoardType {
        @Test
        @DisplayName("boardType에 해당하는 BoardList 정보 반환, 200 OK")
        void testListBoardByBoardType() throws Exception {
            var list = LongStream.range(1L, 4L)
                    .mapToObj(BoardFixtures::board)
                    .toList();

            given(getBoardUseCase.listBoardByBoardType(any()))
                    .willReturn(list);

            // boardType = GENERAL, IMAGE
            mockMvc.perform(
                            get("/boards?boardType={boardType}", BoardType.GENERAL)
                    )
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.size()").value(3),
                            jsonPath("$.[0].boardType").value(BoardType.GENERAL.toString())
                    );
        }
    }

}