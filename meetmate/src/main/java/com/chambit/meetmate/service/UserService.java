package com.chambit.meetmate.service;

import com.chambit.meetmate.dto.OAuthDTO;
import com.chambit.meetmate.dto.UserDTO;
import com.chambit.meetmate.entity.OAuth;
import com.chambit.meetmate.entity.User;
import com.chambit.meetmate.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository ur;
    public UserDTO.Response register(String token){
        OAuthDTO.Response oauth=getKakaoUserInfo(token); //api에서 사용자 정보 가져옴
        //oauth 기준으로 레포를 통해 DB에 있는지 확인
        List<User> isExist= ur.findByUserId(oauth.getInfo().getUser_id());
        //없기에 새로 DB에 저장
        if(isExist.isEmpty()){
            System.out.println("exist");
            Long id=ur.save(User.builder()
                    .userId(oauth.getInfo().getUser_id())
                    .nickname(oauth.getInfo().getNickname())
                    .email(oauth.getInfo().getEmail())
                    .build()).getId();

            return new UserDTO.Response(new UserDTO.Info(id,oauth.getInfo().getUser_id(),
                    oauth.getInfo().getNickname(),oauth.getInfo().getEmail()),
                    200,"success");

        }
        else{//있으니까 가입이 되어있는 상태이므로 이미 존재한다는 결과를 반환
            System.out.println("not_exist");
            return new UserDTO.Response(null,
                    401,"user is already Exist");
        }
    }

    public UserDTO.Response login(String token){
        OAuthDTO.Response oauth=getKakaoUserInfo(token);
        List<User> isExist=ur.findByUserId(oauth.getInfo().getUser_id());

        if(isExist.isEmpty()){ //DB에 값이 없기에 로그인 실패를 넣음
            return new UserDTO.Response(null,
                    401,"login failed");

        }
        //DB에 값이 있기에 로그인 프로세스 실행


        return new UserDTO.Response(UserDTO.Info.builder()
                .user_id(oauth.getInfo().getUser_id())
                .nickname(oauth.getInfo().getNickname())
                .email(oauth.getInfo().getEmail())
                .build(),
                200,"login success");
    }


    private OAuthDTO.Response getKakaoUserInfo(String token){
        String reqURL="https://kapi.kakao.com/v2/user/me";

        try{
            URL url=new URL(reqURL);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();

            //post 요청 설정
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization","Bearer " + token);// 이녀석도 차후 프론트랑 수정해야할듯하다.
            conn.setRequestProperty("charset","utf-8");

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();

            //json으로 나온 response 메시지 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //읽은 json 파싱
            ObjectMapper om=new ObjectMapper();
            Map<String,Object> json=om.readValue(result, new TypeReference<Map<String, Object>>() {});
            Map<String,Object> account=(Map<String, Object>)json.get("kakao_account");
            Map<String,Object> profile=(Map<String, Object>)account.get("profile");


            String email="";
            String id=json.get("id").toString();
            String nickname=profile.get("nickname").toString();

            if(((Boolean) account.get("has_email")).booleanValue()){
                email=account.get("email").toString();
            }

            br.close();

            return new OAuthDTO.Response(OAuthDTO.Info.builder()
                    .user_id(id)
                    .nickname(nickname)
                    .email(email)
                    .build(),200,"success");

        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
