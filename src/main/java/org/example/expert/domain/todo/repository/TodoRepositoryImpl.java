package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne());
    }

    @Override
    public Page<Todo> findAllBySearchOrderByCreatedAtDesc(
            String query,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        JPAQuery<Todo> searchQuery = jpaQueryFactory.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .leftJoin(todo.managers, manager).fetchJoin()
                .where(
                        titleContains(query)
                                .or(managerNameContains(query))
                                .and(createdAtBetween(startDate, endDate))
                ).orderBy(todo.createdAt.desc());

        List<Todo> todos = searchQuery.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(todos, pageable,
                () -> searchTotalCount(query, startDate, endDate));
    }

    @Override
    public Page<Todo> findAllBySearchDateOrderByCreatedAtDesc(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        JPAQuery<Todo> searchDateQuery = jpaQueryFactory.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .leftJoin(todo.managers, manager).fetchJoin()
                .where(createdAtBetween(startDate, endDate))
                .orderBy(todo.createdAt.desc());

        List<Todo> todos = searchDateQuery.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(todos, pageable,
                () -> searchDateTotalCount(startDate, endDate));
    }

    private Long searchTotalCount(String query, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory.select(todo.count())
                .from(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .leftJoin(todo.managers, manager).fetchJoin()
                .where(
                        titleContains(query)
                                .or(managerNameContains(query))
                                .and(createdAtBetween(startDate, endDate))
                ).fetchOne();
    }

    private Long searchDateTotalCount(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory.select(todo.count())
                .from(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .leftJoin(todo.managers, manager).fetchJoin()
                .where(createdAtBetween(startDate, endDate))
                .fetchOne();
    }

    private BooleanExpression titleContains(String query) {
        return query != null ? todo.title.containsIgnoreCase(query) : null;
    }

    private BooleanExpression managerNameContains(String query) {
        return query != null ? manager.user.nickname.containsIgnoreCase(query) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate == null && endDate == null) {
            return null;
        }

        if(startDate != null && endDate != null) {
            return todo.createdAt.between(startDate, endDate);
        }

        return startDate != null ? todo.createdAt.goe(startDate) : todo.createdAt.loe(endDate);
    }
}