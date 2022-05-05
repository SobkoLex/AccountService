package com.sobkolex.account_service.entity;

import com.sobkolex.account_service.model.SecurityEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime date;

    @Enumerated(value = EnumType.STRING)
    private SecurityEvent action;

    @Column
    private String subject;

    private String object;

    private String path;

    public Event(LocalDateTime date, SecurityEvent action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }
}
