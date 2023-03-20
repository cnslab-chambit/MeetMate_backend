package com.chambit.meetmate.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50,nullable = false)
    private String userId;

    @Column(length = 50) //차후 nullable추가 해야할지 의논
    private String nickname;

    @Column(length = 50,nullable = false)
    private String email;

    @Builder
    public User(String userId, String nickname, String email){
        this.userId = userId;
        this.nickname=nickname;
        this.email=email;
    }
}
