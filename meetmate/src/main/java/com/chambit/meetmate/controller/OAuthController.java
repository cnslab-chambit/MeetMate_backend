package com.chambit.meetmate.controller;

import com.chambit.meetmate.service.OAuthService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    OAuthService as;

    //차후 프론트 파트가 구현해줘야 할 기능(Access Token 받는 기능)
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code){
        System.out.println(code);
        as.getAccessToken(code);
    }

    //Access token으로 사용자 정보를 받아오는 기능
    @GetMapping("/user_info")
    public void getKakaoUserInfo(@RequestParam String token){ //인가코드 받음
        as.createKakaoUser(token);
    }


}
