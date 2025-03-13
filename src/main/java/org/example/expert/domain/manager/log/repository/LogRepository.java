package org.example.expert.domain.manager.log.repository;

import org.example.expert.domain.manager.log.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
