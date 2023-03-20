package com.chambit.meetmate.controller;


import com.chambit.meetmate.dto.UserDTO;
import com.chambit.meetmate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserContorller {

    @Autowired
    UserService us;

    @GetMapping("/register")
    public UserDTO.Response createUser(@RequestParam String token){
        return us.register(token);
    }

    @PostMapping("/login")
    public String login(@RequestParam String token, HttpServletRequest request){
        HttpSession session=request.getSession();
        UserDTO.Response login=us.login(token);

        //로그인 상태 체크를 백에서 확인하는건지 잘 모르겠음

        if(login.getReturnCode()==401){
            return login.getReturnMessage();
        }

        session.setAttribute("loginUser",login.getInfo());

        return login.getReturnMessage();
    }

    @PostMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "logout success";
    }
}
