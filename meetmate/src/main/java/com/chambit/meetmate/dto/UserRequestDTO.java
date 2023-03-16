package com.chambit.meetmate.dto;

import com.chambit.meetmate.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDTO {
    private String id;
    private String nickname;
    private String email;

    @Builder
    public UserRequestDTO(String id,String nickname,String email){
        this.id=id;
        this.nickname=nickname;
        this.email=email;
    }

    public UserEntity toEntity(){
        return UserEntity.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .build();
    }
}
