package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryQuery {

    Page<User> findAllByNickname(String query, Pageable pageable);
}
