package com.chambit.meetmate.dto;

import com.chambit.meetmate.entity.OAuthEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthRequestDTO {
    private String id;
    private String nickname;
    private String email;

    @Builder
    public OAuthRequestDTO(String id,String nickname,String email){
        this.id=id;
        this.nickname=nickname;
        this.email=email;
    }

    public OAuthEntity toEntity(){
        return OAuthEntity.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .build();
    }
}
