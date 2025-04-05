package matching.teamify.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import matching.teamify.config.SessionConst;
import matching.teamify.dto.member.MemberSignUpRequest;
import matching.teamify.dto.member.MemberSignUpResponse;
import matching.teamify.dto.member.MyPageResponse;
import matching.teamify.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<MemberSignUpResponse> signUp(@RequestBody MemberSignUpRequest memberDto) {

        MemberSignUpResponse memberSignUpResponse = memberService.createMember(memberDto);

        return ResponseEntity.ok(memberSignUpResponse);
    }

    @GetMapping("/members/profile")
    public ResponseEntity<MyPageResponse> showProfile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);

        MyPageResponse myPageInfo = memberService.getMyPageInfo(memberId);

        return ResponseEntity.ok(myPageInfo);
    }


}
