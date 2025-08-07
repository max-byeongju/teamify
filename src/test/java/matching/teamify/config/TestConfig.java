package matching.teamify.config;

import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectRepository;
import matching.teamify.repository.StudyRepository;
import matching.teamify.service.ProjectApplyTestDataSetupService;
import matching.teamify.service.StudyApplyTestDataSetupService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public StudyApplyTestDataSetupService studyTestDataSetupService(MemberRepository memberRepository, StudyRepository studyRepository) {
        return new StudyApplyTestDataSetupService(memberRepository, studyRepository);
    }

    @Bean
    public ProjectApplyTestDataSetupService projectTestDataSetupService(MemberRepository memberRepository, ProjectRepository projectRepository) {
        return new ProjectApplyTestDataSetupService(memberRepository, projectRepository);
    }
}
