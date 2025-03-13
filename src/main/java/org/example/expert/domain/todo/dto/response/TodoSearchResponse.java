package org.example.expert.domain.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoSearchResponse {

    private final Long id;
    private final String title;
    private final Integer managerCount;
    private final Integer commentCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd hh:mm")
    private final LocalDateTime createdAt;

    public static TodoSearchResponse of(Todo todo) {
        return new TodoSearchResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getManagers().size(),
                todo.getComments().size(),
                todo.getCreatedAt()
        );
    }
}
