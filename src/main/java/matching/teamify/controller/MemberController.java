package matching.teamify.controller;

import lombok.RequiredArgsConstructor;
import matching.teamify.dto.member.MemberSignupRequest;
import matching.teamify.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/new")
    public void signUp(@RequestBody MemberSignupRequest memberDto) {
        memberService.createMember(memberDto);
    }






}
