package com.ssafy.deathnotelive.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "participants",
        indexes = {
                @Index(columnList = "id"),
                @Index(columnList = "nickName"),
                @Index(columnList = "roomCode"),
        })
@Entity
public class Participants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String nickName;

    @ManyToOne
    @JoinColumn(name = "roomCode")
    private Room room;
}
