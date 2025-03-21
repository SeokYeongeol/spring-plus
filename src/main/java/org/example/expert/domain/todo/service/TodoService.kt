package org.example.expert.domain.todo.service

import org.example.expert.client.WeatherClient
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.todo.dto.request.TodoSaveRequest
import org.example.expert.domain.todo.dto.response.TodoResponse
import org.example.expert.domain.todo.dto.response.TodoSaveResponse
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.todo.repository.TodoRepository
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class TodoService(
    val todoRepository: TodoRepository,
    val weatherClient: WeatherClient
) {

    @Transactional
    fun saveTodo(authUser: AuthUser, todoSaveRequest: TodoSaveRequest): TodoSaveResponse {
        val user: User = User.fromAuthUser(authUser)
        val weather: String = weatherClient.getTodayWeather()

        val newTodo = Todo(
            title = todoSaveRequest.title,
            contents = todoSaveRequest.contents,
            weather = weather,
            user = user
        ).copy()
        val savedTodo = todoRepository.save(newTodo)

        return TodoSaveResponse(
            savedTodo.id,
            savedTodo.title,
            savedTodo.contents,
            savedTodo.weather,
            UserResponse(user.id, user.email, user.nickname)
        );
    }

    @Transactional(readOnly = true)
    fun getTodos(page: Int, size: Int, weather: String?): Page<TodoResponse> {
        val pageable: Pageable = PageRequest.of(page - 1, size)

        val todos = if(weather.isNullOrEmpty()) {
            todoRepository.findAllByOrderByModifiedAtDesc(pageable)
        } else {
            todoRepository.findAllByWeatherOrderByModifiedAtDesc(weather, pageable)
        }

        return todos!!.map { todo: Todo? ->
            TodoResponse(
                todo!!.id,
                todo.title,
                todo.contents,
                todo.weather,
                UserResponse(todo.user.id, todo.user.email, todo.user.nickname),
                todo.createdAt,
                todo.modifiedAt
            )
        }
    }

    @Transactional(readOnly = true)
    fun getTodo(todoId: Long): TodoResponse {
        val todo: Todo? = todoRepository.findByIdWithUser(todoId)
            .orElseThrow { InvalidRequestException("Todo not Found") }
        val user: User = todo!!.user

        return TodoResponse(
            todo.id,
            todo.title,
            todo.contents,
            todo.weather,
            UserResponse(user.id, user.email, user.nickname),
            todo.createdAt,
            todo.modifiedAt
        )
    }

    @Transactional(readOnly = true)
    fun getSearchTodos(
        page: Int,
        size: Int,
        query: String?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): Page<TodoSearchResponse>
    {
        val pageable: Pageable = PageRequest.of(page - 1, size)

        val todos = if(query.isNullOrEmpty()) {
            todoRepository.findAllBySearchDateOrderByCreatedAtDesc(startDate, endDate, pageable)
        } else {
            todoRepository.findAllBySearchOrderByCreatedAtDesc(query, startDate, endDate, pageable)
        }

        return todos.map { todo: Todo? -> TodoSearchResponse.of(todo) }
    }
}