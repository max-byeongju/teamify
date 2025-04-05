package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import matching.teamify.dto.member.MemberSignupRequest;
import matching.teamify.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void createMember(MemberSignupRequest requestDto) {
        String pw = passwordEncoder.encode(requestDto.getPassword());

        Member member = Member.builder()
                .loginId(requestDto.getLoginId())
                .password(pw)
                .nickName(requestDto.getNickName())
                .university(requestDto.getUniversity())
                .email(requestDto.getEmail())
                .build();

        memberRepository.save(member);

    }

}
