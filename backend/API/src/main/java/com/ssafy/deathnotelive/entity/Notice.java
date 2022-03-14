package com.ssafy.deathnotelive.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notice",
        indexes = {
                @Index(columnList = "id"),
                @Index(columnList = "userId"),
                @Index(columnList = "title"),
                @Index(columnList = "content"),
                @Index(columnList = "hit"),
                @Index(columnList = "createdAt"),
                @Index(columnList = "modifiedAt"),
        })
@Entity
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Setter
    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String title;

    @Setter
    @Column(nullable = false, columnDefinition = "varchar(2000)")
    private String content;

    @Setter
    @Column(nullable = false)
    private Integer hit;


    @Column(nullable = false, insertable = false, updatable = false,
            columnDefinition = "datetime default CURRENT_TIMESTAMP")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false, insertable = false, updatable = false,
            columnDefinition = "datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

}