package com.chambit.meetmate.dto;

import com.chambit.meetmate.entity.OAuthEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OAuthResponseDTO {

    private String id;
    private String nickname;
    private String email;

    @Builder
    public OAuthResponseDTO(OAuthEntity oauthEntity){
        this.id=oauthEntity.getId();
        this.nickname=oauthEntity.getNickname();
        this.email=oauthEntity.getEmail();
    }
}
