package com.ssafy.deathnotelive.entity;

import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room",
        indexes = {
                @Index(columnList = "id"),
                @Index(columnList = "roomCode"),
                @Index(columnList = "isOpen"),
                @Index(columnList = "userId"),
        })
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @Unique
    @Column(nullable = false, columnDefinition = "varchar(10)")
    private String roomCode;

    @Setter
    @Column(nullable = false, columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isOpen = true;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

}
