package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import matching.teamify.dto.member.MemberSignUpRequest;
import matching.teamify.dto.member.MemberSignUpResponse;
import matching.teamify.dto.member.MyPageResponse;
import matching.teamify.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberSignUpResponse createMember(MemberSignUpRequest requestDto) {

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

        memberRepository.save(member);

        return new MemberSignUpResponse(member.getId());
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId);

        if (member == null) {
            throw new RuntimeException("해당 멤버를 찾을 수 없습니다");
        }

        return member;

    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(Long memberId) {
        Member member = getMemberById(memberId);

        return new MyPageResponse(member.getNickName(), member.getUniversity(), member.getEmail(), member.getPicture());

    }



}
