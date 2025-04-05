package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import matching.teamify.dto.member.MemberSignupRequest;
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
    public void createMember(MemberSignupRequest requestDto) {

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

    }

}
