package com.chambit.meetmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class OAuthDTO {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Info{
        private String user_id;
        private String nickname;
        private String email;
    }

    @Getter
    @Setter
    public static class Request{
        private String user_id;
        private String nickname;
        private String email;
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private Info info;
        private int returnCode;
        private String returnMessage;
    }
}
