package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.domain.Member;
import matching.teamify.domain.Study;
import matching.teamify.domain.StudyApplication;
import matching.teamify.dto.apply.StudyApplicantResponse;
import matching.teamify.dto.apply.StudyApplicationResponse;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.StudyApplicationRepository;
import matching.teamify.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyApplicationService {

    private final StudyApplicationRepository studyApplicationRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

    @Value("${app.default-profile-image-url}")
    private String defaultProfileImageUrl;

    @Transactional
    public void applyToStudy(Long studyId, Long memberId) {
        Study applyStudy = studyRepository.findById(studyId);
        Member applyMember = memberRepository.findById(memberId);
        if (!applyStudy.isRecruiting()) {
            throw new RuntimeException("이미 마감된 스터디입니다.");
        }
        if (Objects.equals(memberId, applyStudy.getMember().getId())) {
            throw new RuntimeException("본인의 스터디에는 지원할 수 없습니다.");
        }
        if (applyStudy.getRecruitNumber() == applyStudy.getParticipants()) {
            throw new RuntimeException("스터디 모집 인원이 가득 찼습니다.");
        }
        applyStudy.addParticipant();
        studyApplicationRepository.save(applyStudy, applyMember);
    }

    @Transactional(readOnly = true)
    public List<StudyApplicationResponse> findAppliedStudies(Long memberId) {
        return studyApplicationRepository.findAppliedStudyByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<StudyApplicantResponse> findApplicantsForStudy(Long studyId) {
        List<StudyApplicantResponse> studyApplicantResponseList = studyApplicationRepository.findApplyMemberByStudyId(studyId);
        for (StudyApplicantResponse applyMember : studyApplicantResponseList) {
            String s3Key = applyMember.getImageUrl();
            if (s3Key == null || s3Key.trim().isEmpty()) {
                applyMember.setImageUrl(defaultProfileImageUrl);
            } else {
                String imageUrl = s3ImageService.getImageUrl(s3Key);
                applyMember.setImageUrl(imageUrl);
            }
        }
        return studyApplicantResponseList;
    }

    @Transactional
    public void cancelApply(Long memberId, Long studyId) {
        Study appliedStudy = studyRepository.findById(studyId);
        StudyApplication studyApplication = studyApplicationRepository.findByMemberIdAndStudyId(memberId, studyId)
                .orElseThrow(() -> new RuntimeException("지원 정보를 찾을 수 없습니다."));

        if (appliedStudy.getParticipants() > 0) {
            appliedStudy.removeParticipant();
        } else {
            log.warn("스터디 ID {}: 지원자 수가 이미 0이므로 감소 작업을 수행하지 않았습니다.", studyId);
        }
        studyApplicationRepository.removeStudyApplication(studyApplication);
    }
}
