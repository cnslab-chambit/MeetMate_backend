package com.chambit.meetmate.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
public class OAuthService {

    @Value("${authApi-key}")
    private String apikey;


    public String getAccessToken (String code){
        String accessToken="";
        String refreshToken="";
        String reqURL="https://kauth.kakao.com/oauth/token";


        try{
            URL url=new URL(reqURL);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb=new StringBuilder();

            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+apikey); //rest apikey. 차후 숨겨야함
            sb.append("&redirect_uri=http://localhost:8080/oauth/kakao");
            sb.append("&code="+code);

            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //json으로 나온 response 메시지 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //읽은 json 파싱
            ObjectMapper om=new ObjectMapper();
            Map<String,String> json=om.readValue(result,Map.class);

            System.out.println("access_token : "+json.get("access_token"));
            System.out.println("refresh_token : "+json.get("refresh_token"));

            br.close();
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return accessToken;
    }

    public void createKakaoUser (String token){
        String reqURL="https://kapi.kakao.com/v2/user/me";
        System.out.println(token);

        try{
            URL url=new URL(reqURL);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();

            //post 요청 설정
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization","Bearer " + token);// 이녀석도 차후 프론트랑 수정해야할듯하다.

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //json으로 나온 response 메시지 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

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
            System.out.println(id);
            System.out.println(email);

            br.close();


        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
