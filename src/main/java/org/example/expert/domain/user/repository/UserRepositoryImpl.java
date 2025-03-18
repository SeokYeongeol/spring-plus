package org.example.expert.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<User> findAllByNickname(String query, Pageable pageable) {
        JPAQuery<User> searchUsers = jpaQueryFactory.selectFrom(user)
                .where(nicknameContains(query))
                .orderBy(user.createdAt.desc());

        List<User> users = searchUsers.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = Optional.ofNullable(
                jpaQueryFactory.select(user.count())
                .from(user)
                .where(nicknameContains(query))
                .fetchOne()
        ).orElse(0L);

        return PageableExecutionUtils.getPage(users, pageable, () -> totalCount);
    }

    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null ? user.nickname.containsIgnoreCase(nickname) : Expressions.TRUE;
    }
}
