package com.practice.test.board.article.application.service;

import com.practice.test.board.article.application.port.out.LoadBoardPort;
import com.practice.test.board.article.domain.BoardFixtures;
import com.practice.test.board.article.domain.BoardType;
import com.practice.test.board.common.exception.ResourceNotFoundException;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * - getBoard(boardId)
 *  - Repository에서 조회되면 PersistenceAdapter에서 Board 반환
 *  - Repository에서 조회되지 않으면 ResourceNotFoundException 발생
 */
class BoardServiceTest {

    private BoardService sut;
    private LoadBoardPort boardPort = Mockito.mock(LoadBoardPort.class);

    @BeforeEach
    void setUp() {
        sut = new BoardService(boardPort);
    }

    @Nested
    @DisplayName("get getBoard(boardId)")
    class GetBoard {
        @Test
        @DisplayName("Board가 존재하면 Board 반환")
        void givenBoardThenReturnBoard() {
            // given
            var boardId = 1L;
            var board = BoardFixtures.board(boardId);
            given(boardPort.findBoardById(any()))
                    .willReturn(Optional.of(board));

            // when
            var result = sut.getBoard(boardId);

            // then
            then(result).isNotNull()
                    .hasFieldOrPropertyWithValue("id", boardId)
                    .hasFieldOrPropertyWithValue("name", board.getName());

        }

        @Test
        @DisplayName("Board가 존재하지 않으면 ResourceNotFoundException 발생")
        void givenNotExistBoardThenThrowResourceNotFoundException() {
            // given
            var boardId = 1L;
            given(boardPort.findBoardById(any()))
                    .willReturn(Optional.empty());

            // when
            // then
            BDDAssertions.thenThrownBy(() -> sut.getBoard(boardId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("TEST listBoardByBoardType(boardType)")
    class ListBoardsByBoardType {
        @Test
        @DisplayName("boardType에 해당하는 Board 리스트 반환")
        void returnBoardList() {
            var list = LongStream.range(1L, 4L)
                    .mapToObj(BoardFixtures::board)
                    .toList();

            // given
            given(boardPort.findBoardByBoardType(any()))
                    .willReturn(list);

            // when
            var result = sut.listBoardByBoardType(BoardType.GENERAL);

            // then
            then(result)
                    .hasSize(3)
                    .extracting("boardType")
                    .containsOnly(BoardType.GENERAL);
        }
    }
}