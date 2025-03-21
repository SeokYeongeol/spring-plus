//package org.example.expert.domain.manager.log.entity;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Entity
//@Table(name = "logs")
//public class Log {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String message;
//
//    @Column(updatable = false)
//    @Temporal(TemporalType.TIMESTAMP)
//    private LocalDateTime createdAt;
//
//    public Log(String message) {
//        this.message = message;
//        this.createdAt = LocalDateTime.now();
//    }
//}