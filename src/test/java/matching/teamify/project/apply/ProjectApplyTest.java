package matching.teamify.project.apply;

import lombok.extern.slf4j.Slf4j;
import matching.teamify.common.exception.ErrorCode;
import matching.teamify.common.exception.TeamifyException;
import matching.teamify.domain.Member;
import matching.teamify.domain.Project;
import matching.teamify.domain.ProjectRole;
import matching.teamify.dto.ProjectApplyTestData;
import matching.teamify.dto.apply.ProjectApplicationRequest;
import matching.teamify.repository.ProjectRepository;
import matching.teamify.service.ProjectApplicationService;
import matching.teamify.service.ProjectApplyTestDataSetupService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
public class ProjectApplyTest {

    @Autowired
    private ProjectApplicationService projectApplicationService;
    @Autowired
    private ProjectApplyTestDataSetupService projectApplyTestDataSetupService;
    @Autowired
    private ProjectRepository projectRepository;

    private Project targetProject;
    private List<Member> applicants;

    @BeforeEach
    void setUp() {
        ProjectApplyTestData testData = projectApplyTestDataSetupService.setupForProjectApplicationTest();
        this.targetProject = testData.getTargetProject();
        this.applicants = testData.getApplicants();
    }

    @Test
    @DisplayName("30명이 동시에 역할별 정원이 다른 프로젝트에 지원 - 조건부 Update")
    void applyToProject_concurrency_test() throws InterruptedException {
        // given
        int threadCount = applicants.size();
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        ProjectRole[] roles = {ProjectRole.FRONTEND, ProjectRole.BACKEND, ProjectRole.DESIGNER};

        // when
        for (int i = 0; i < threadCount; i++) {
            final Member applicant = applicants.get(i);
            ProjectRole role = roles[i % 3];
            ProjectApplicationRequest request = new ProjectApplicationRequest("열심히 하겠습니다.", role);

            es.submit(() -> {
                try {
                    projectApplicationService.applyToProject(targetProject.getId(), applicant.getId(), request);

                } catch (Exception e) {
                    log.info("지원 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        es.shutdown();

        // then
        Project finalProject = projectRepository.findById(targetProject.getId())
                .orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));

        int totalApplied = finalProject.getFrontApplyNumber()
                + finalProject.getBackApplyNumber()
                + finalProject.getDesignApplyNumber();

        int maxAllowedApplications = targetProject.getMaxApplicationsForFrontend()
                + targetProject.getMaxApplicationForBackend()
                + targetProject.getMaxApplicationForDesigner();

        assertThat(totalApplied).isEqualTo(maxAllowedApplications);
        assertThat(finalProject.getFrontApplyNumber()).isEqualTo(targetProject.getMaxApplicationsForFrontend());
        assertThat(finalProject.getBackApplyNumber()).isEqualTo(targetProject.getMaxApplicationForBackend());
        assertThat(finalProject.getDesignApplyNumber()).isEqualTo(targetProject.getMaxApplicationForDesigner());
    }

    @AfterEach
    @Sql("/project-apply-cleanup.sql")
    void tearDown() {}

}
