package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.domain.Member;
import matching.teamify.dto.member.MemberSignUpRequest;
import matching.teamify.dto.member.MemberSignUpResponse;
import matching.teamify.dto.member.MyPageRequest;
import matching.teamify.dto.member.MyPageResponse;
import matching.teamify.exception.common.EntityNotFoundException;
import matching.teamify.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-profile-image-url}")
    private String defaultProfileImageUrl;

    @Transactional
    public Long createMember(MemberSignUpRequest requestDto) {
        Optional<Member> byLoginId = memberRepository.findByLoginId(requestDto.getLoginId());
        if (byLoginId.isPresent()) {
            throw new RuntimeException("이미 사용중인 ID 입니다.");
        }
        String pw = passwordEncoder.encode(requestDto.getPassword());
        Member member = Member.builder()
                .loginId(requestDto.getLoginId())
                .password(pw)
                .nickName(requestDto.getNickName())
                .university(requestDto.getUniversity())
                .email(requestDto.getEmail())
                .build();
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member", memberId));
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(Long memberId) {
        Member member = getMemberById(memberId);
        MyPageResponse myPageResponse = translateMyPageResponse(member);
        String s3Key = member.getPicture();
        if (s3Key == null || s3Key.trim().isEmpty()) {
            myPageResponse.setImageUrl(defaultProfileImageUrl);
        } else {
            String imageUrl = s3ImageService.getImageUrl(s3Key);
            myPageResponse.setImageUrl(imageUrl);
        }
        return myPageResponse;
    }

    @Transactional
    public void updateProfile(Long memberId, MyPageRequest myPageRequest) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        if (myPageRequest.getImageUrl() == null) {
            member.updateProfileNotImage(myPageRequest.getNickName(), myPageRequest.getUniversity(), myPageRequest.getEmail());
        } else {
            member.updateProfile(myPageRequest.getImageUrl(), myPageRequest.getNickName(), myPageRequest.getUniversity(), member.getEmail());
        }
    }

    private MyPageResponse translateMyPageResponse(Member member) {
        String nickName = member.getNickName();
        String university = member.getUniversity();
        String email = member.getEmail();
        return new MyPageResponse(nickName, university, email);
    }
}
