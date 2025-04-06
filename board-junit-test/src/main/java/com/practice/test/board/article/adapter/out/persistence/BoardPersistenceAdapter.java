package com.practice.test.board.article.adapter.out.persistence;

import com.practice.test.board.article.adapter.out.persistence.entity.BoardJpaEntity;
import com.practice.test.board.article.adapter.out.persistence.repository.BoardRepository;
import com.practice.test.board.article.application.port.out.LoadBoardPort;
import com.practice.test.board.article.domain.Board;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BoardPersistenceAdapter implements LoadBoardPort {

    private final BoardRepository boardRepository;

    public BoardPersistenceAdapter(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public Optional<Board> findBoardById(Long boardId) {
        return boardRepository.findById(boardId).map(BoardJpaEntity::toDomain);
    }
}
