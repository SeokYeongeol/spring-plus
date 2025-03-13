package org.example.expert.domain.manager.log.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.manager.log.entity.Log;
import org.example.expert.domain.manager.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.SUPPORTS)
    public void saveLog(String message) {
        log.info("매니저 등록 요청이 왔습니다");
        logRepository.save(new Log(message));
    }
}
