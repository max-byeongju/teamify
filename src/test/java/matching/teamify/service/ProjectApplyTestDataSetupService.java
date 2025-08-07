package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import matching.teamify.domain.Project;
import matching.teamify.dto.ProjectApplyTestData;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectApplyTestDataSetupService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectApplyTestData setupForProjectApplicationTest() {
        Member host = memberRepository.save(new Member("host", "pw", "주최자", "학교", "host@email.com"));

        // 역할별 모집 인원 설정
        Project project = Project.builder()
                .title("Concurrency Test Project")
                .field("IT")
                .techStack("JPA, Java, Spring")
                .frontendNumber(2)  // 프론트엔드 2명 모집 -> 최대 6명 지원 가능
                .backendNumber(2)   // 백엔드 2명 모집 -> 최대 6명 지원 가능
                .designerNumber(1)  // 디자이너 1명 모집 -> 최대 3명 지원 가능
                .content("Test Content")
                .build();
        project.createProject(host);
        projectRepository.save(project);

        // 총 15명(6+6+3)의 지원 슬롯을 초과하는 30명의 지원자 생성
        List<Member> applicants = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Member newMember = new Member("user" + i, "pw", "user" + i, "학교", "user" + i + "@email.com");
            applicants.add(memberRepository.save(newMember));
        }

        return new ProjectApplyTestData(project, applicants);
    }
}
