package matching.teamify.study.apply;

import lombok.extern.slf4j.Slf4j;
import matching.teamify.common.exception.ErrorCode;
import matching.teamify.common.exception.TeamifyException;
import matching.teamify.domain.Member;
import matching.teamify.domain.Study;
import matching.teamify.dto.StudyApplyTestData;
import matching.teamify.repository.StudyRepository;
import matching.teamify.service.StudyApplicationService;
import matching.teamify.service.StudyApplyTestDataSetupService;
import org.assertj.core.api.Assertions;
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

@Slf4j
@SpringBootTest
public class StudyApplyTest {

    @Autowired
    private StudyApplicationService studyApplicationService;

    @Autowired
    private StudyApplyTestDataSetupService studyApplyTestDataSetupService;

    @Autowired
    private StudyRepository studyRepository;

    private Study targetStudy;
    private List<Member> applicants;

    @BeforeEach
    void setUp() {
        StudyApplyTestData studyApplyTestData = studyApplyTestDataSetupService.setupForStudyApplicationTest();
        this.targetStudy = studyApplyTestData.getTargetStudy();
        this.applicants = studyApplyTestData.getApplicants();
    }

    @Test
    @DisplayName("30명이 동시에 모집인원 10명인 스터디에 지원")
    void applyToStudy_concurrency_test() throws InterruptedException {
        // given
        int threadCount = applicants.size();
        ExecutorService es = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (Member applicant : applicants) {
            es.submit(() -> {
                try {
                    studyApplicationService.applyToStudy(targetStudy.getId(), applicant.getId());
                } catch (Exception e) {
                    log.info("지원 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        es.shutdown();

        //then
        Study finalStudy = studyRepository.findById(targetStudy.getId()).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Assertions.assertThat(finalStudy.getParticipants()).isEqualTo(targetStudy.getRecruitNumber());
    }


    @AfterEach
    @Sql("/study-apply-cleanup.sql")
    void tearDown() {}

}
