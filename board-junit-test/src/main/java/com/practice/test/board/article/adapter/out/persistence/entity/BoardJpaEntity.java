package com.practice.test.board.article.adapter.out.persistence.entity;

import com.practice.test.board.article.domain.Board;
import com.practice.test.board.article.domain.BoardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board")
@NoArgsConstructor
@Getter
public class BoardJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "board_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    public BoardJpaEntity(String name) {
        this.name = name;
    }

    public BoardJpaEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public BoardJpaEntity(String name, BoardType boardType) {
        this.name = name;
        this.boardType = boardType;
    }

    public Board toDomain() {
        return new Board(this.id, this.name, this.boardType);
    }

    public static BoardJpaEntity fromDomain(Board board) {
        return new BoardJpaEntity(board.getId(), board.getName());
    }

}
