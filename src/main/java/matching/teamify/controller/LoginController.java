package matching.teamify.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.config.SessionConst;
import matching.teamify.domain.Member;
import matching.teamify.dto.login.LoginRequest;
import matching.teamify.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/auth/login")
    public void login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        Member loginMember = loginService.login(loginRequest.getLoginId(), loginRequest.getPassword());

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember.getId());

    }

    @PostMapping("/auth/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

    }

}
