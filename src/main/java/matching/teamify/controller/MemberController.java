package matching.teamify.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import matching.teamify.config.SessionConst;
import matching.teamify.config.auth.Login;
import matching.teamify.domain.Member;
import matching.teamify.dto.member.MemberSignUpRequest;
import matching.teamify.dto.member.MemberSignUpResponse;
import matching.teamify.dto.member.MyPageRequest;
import matching.teamify.dto.member.MyPageResponse;
import matching.teamify.service.MemberService;
import matching.teamify.service.S3ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final S3ImageService s3ImageService;

    @PostMapping("/members")
    public ResponseEntity<Void> signUp(@RequestBody MemberSignUpRequest memberDto) {
        Long memberId = memberService.createMember(memberDto);
        return ResponseEntity.created(URI.create("/members/" + memberId)).build();
    }

    @GetMapping("/members/profile")
    public ResponseEntity<MyPageResponse> showProfile(@Login Long memberId) {
        MyPageResponse myPageInfo = memberService.getMyPageInfo(memberId);
        return ResponseEntity.ok(myPageInfo);
    }

    @PostMapping("/members/profile")
    public ResponseEntity<Void> changeProfile(
            @ModelAttribute MyPageRequest myPageRequest,
            @RequestParam(required = false) MultipartFile form,
            @Login Long memberId) {
        Member member = memberService.getMemberById(memberId);
        String oldProfileImageIdentifier = member.getPicture();
        if (form != null && !form.isEmpty()) {
            myPageRequest.setImageUrl(s3ImageService.uploadImage(form));
        }
        memberService.updateProfile(memberId, myPageRequest);
        if (myPageRequest.getImageUrl() != null && oldProfileImageIdentifier != null && !oldProfileImageIdentifier.isEmpty()) {
            s3ImageService.deleteImage(oldProfileImageIdentifier);
        }
        return ResponseEntity.noContent().build();
    }
}
