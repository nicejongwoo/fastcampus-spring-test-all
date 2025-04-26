INSERT INTO board (id, name, board_type)
VALUES (5, 'board', 'GENERAL');

INSERT INTO article (board_id, subject, content, username, created_At)
VALUES (5, 'subject1', 'content2', 'user', now()),
       (5, 'subject2', 'content2', 'user', now()),
       (5, 'subject2', 'content2', 'user', now());