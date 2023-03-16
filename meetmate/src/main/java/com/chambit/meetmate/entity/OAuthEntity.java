package com.chambit.meetmate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
public class OAuthEntity {
    @Id
    private String id;
    private String nickname;
    private String email;

    @Builder
    public OAuthEntity(String id,String nickname,String email){
        this.id=id;
        this.nickname=nickname;
        this.email=email;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }
}
