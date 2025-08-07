package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import matching.teamify.domain.Study;
import matching.teamify.dto.StudyApplyTestData;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyApplyTestDataSetupService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;

    @Transactional
    public StudyApplyTestData setupForStudyApplicationTest() {

        Member host = memberRepository.save(new Member("id1", "pw1", "회원1", "홍익대학교", "id1@gmail.com"));
        Study study = new Study("Study1", 10, "내용");

        study.createStudy(host);
        studyRepository.save(study);

        List<Member> applicants = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Member newMember = new Member("user" + i, "pw", "user" + i, "홍익대학교", "user" + i + "@gmail.com");
            Member savedMember = memberRepository.save(newMember);
            applicants.add(savedMember);
        }

        return new StudyApplyTestData(study, applicants);
    }
}
