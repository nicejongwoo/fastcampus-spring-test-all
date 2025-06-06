package com.practice.test.board.article.adapter.in.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.test.board.article.adapter.in.api.dto.ArticleDto;
import com.practice.test.board.article.application.port.in.CreateArticleUseCase;
import com.practice.test.board.article.application.port.in.DeleteArticleUseCase;
import com.practice.test.board.article.application.port.in.GetArticleUseCase;
import com.practice.test.board.article.application.port.in.ModifyArticleUseCase;
import com.practice.test.board.article.domain.ArticleFixtures;
import com.practice.test.board.common.exception.AccessDeniedException;
import com.practice.test.board.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArticleController.class)
public class ArticleControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GetArticleUseCase getArticleUseCase;

    @MockitoBean
    private CreateArticleUseCase createArticleUseCase;

    @MockitoBean
    private ModifyArticleUseCase modifyArticleUseCase;

    @MockitoBean
    private DeleteArticleUseCase deleteArticleUseCase;

    @Nested
    @DisplayName("GET /articles/{articleId}")
    class GetArticle {
        @Test
        @DisplayName("article이 있으면 200 OK return response")
        void returnResponse() throws Exception {
            var article = ArticleFixtures.article();
            given(getArticleUseCase.getArticleById(any()))
                    .willReturn(article);

            mockMvc.perform(get("/articles/{articleId}", 1L))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("articleId 에 해당하는 Article 이 없으면 400 Not Found")
        void returnNotFound() throws Exception {
            given(getArticleUseCase.getArticleById(any()))
                    .willThrow(new ResourceNotFoundException("id: " + 1L + " 게시물이 없습니다."));

            mockMvc.perform(get("/articles/{articleId}", 1L))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("GET /articles?boardId={boardId}")
    void listArticlesByBoardId() throws Exception {
        given(getArticleUseCase.getArticlesByBoard(any()))
                .willReturn(List.of(ArticleFixtures.article(1L), ArticleFixtures.article(2L)));

        mockMvc.perform(get("/articles?boardId={boardId}", 5L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Nested
    @DisplayName("POST /articles")
    class PostArticle {
        @Test
        @DisplayName("생성된 articleId 반환")
        void returnArticleId() throws Exception {
            var createdArticle = ArticleFixtures.article();
            given(createArticleUseCase.createArticle(any()))
                    .willReturn(createdArticle);

            var body = objectMapper.writeValueAsString(Map.of("boardId",
                    5L,
                    "subject",
                    "subjectTest",
                    "content",
                    "contentTest",
                    "username",
                    "user"));
            mockMvc.perform(
                    post("/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @ParameterizedTest(name = "{0}")
        @DisplayName("비정상 파마리터면 BadRequest")
        @CsvSource(
                value = {
                        "subject is null,,content,user",
                        "content is null,subject,,user",
                        "username is null,subject,content,",
                        "username is empty,subject,content,''",
                }
        )
        void invalidParam_BadRequest(String desc, String subject, String content, String username) throws Exception {
            var body = objectMapper.writeValueAsString(new ArticleDto.CreateArticleRequest(5L, subject, content, username));

            mockMvc.perform(
                    post("/articles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

        }
    }

    @Nested
    @DisplayName("PUT /articles")
    class PutArticle {
        @Test
        @DisplayName("변경된 Article의 articleId 반환")
        void returnArticleId() throws Exception {
            var modifiedArticle = ArticleFixtures.article();
            given(modifyArticleUseCase.modifyArticle(any()))
                    .willReturn(modifiedArticle);

            var body = objectMapper.writeValueAsString(Map.of("id", 1L, "board", Map.of("id", 5L, "name", "board"), "subject", "new subject", "content", "new content", "username", "user"));
            mockMvc
                    .perform(
                            put("/articles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body)
                    )
                    .andDo(print())
                    .andExpect(
                            status().isOk()
                    );
        }

        @ParameterizedTest(name = "{0}")
        @CsvSource(
                value = {
                        "subject is null,,content,user",
                        "content is null,subject,,user"
                }
        )
        void invalidParam_BadRequest(String desc, String subject, String content, String username) throws Exception {
            var body = objectMapper.writeValueAsString(new ArticleDto.CreateArticleRequest(5L, subject, content, username));
            mockMvc
                    .perform(
                            put("/articles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body)
                    )
                    .andDo(print())
                    .andExpect(
                            status().isBadRequest()
                    );
        }

        @Test
        void otherUser_Forbidden() throws Exception {
            given(modifyArticleUseCase.modifyArticle(any()))
                    .willThrow(new AccessDeniedException("다른 작성자는 수정 불가능"));

            var body = objectMapper.writeValueAsString(Map.of("id", 1L, "board", Map.of("id", 5L, "name", "board"), "subject", "new subject", "content", "new content", "username", "otheruser"));
            mockMvc
                    .perform(
                            put("/articles")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body)
                    )
                    .andDo(print())
                    .andExpect(
                            status().isForbidden()
                    );
        }
    }

    @Test
    @DisplayName("DELETE /articles/{articleId}")
    void deleteArticle() throws Exception {
        willDoNothing().given(deleteArticleUseCase).deleteArticle(any());

        mockMvc.perform(delete("/articles/{articleId}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
