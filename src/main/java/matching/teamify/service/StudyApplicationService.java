package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.domain.Member;
import matching.teamify.domain.Study;
import matching.teamify.domain.StudyApplication;
import matching.teamify.dto.apply.StudyApplicantResponse;
import matching.teamify.dto.apply.StudyApplicationResponse;
import matching.teamify.common.exception.ErrorCode;
import matching.teamify.common.exception.TeamifyException;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.StudyApplicationRepository;
import matching.teamify.repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyApplicationService {

    private final StudyApplicationRepository studyApplicationRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public void applyToStudy(Long studyId, Long memberId) {
        Study applyStudy = studyRepository.findByIdWithLock(studyId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Member applyMember = memberRepository.findById(memberId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));

        Optional<StudyApplication> existingApplication = studyApplicationRepository.findByMemberIdAndStudyId(memberId, studyId);
        if (existingApplication.isPresent()) {
            throw new TeamifyException(ErrorCode.APPLICATION_ALREADY_EXISTS);
        }

        if (!applyStudy.isRecruiting()) {
            throw new TeamifyException(ErrorCode.RECRUITMENT_CLOSED);
        }
        if (Objects.equals(memberId, applyStudy.getMember().getId())) {
            throw new TeamifyException(ErrorCode.CANNOT_APPLY_TO_OWN_RECRUITMENT);
        }
        if (applyStudy.getRecruitNumber() == applyStudy.getParticipants()) {
            throw new TeamifyException(ErrorCode.RECRUITMENT_FULL);
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
            applyMember.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
        }
        return studyApplicantResponseList;
    }

    @Transactional
    public void cancelApply(Long memberId, Long studyId) {
        Study appliedStudy = studyRepository.findById(studyId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        StudyApplication studyApplication = studyApplicationRepository.findByMemberIdAndStudyId(memberId, studyId)
                .orElseThrow(() -> new TeamifyException(ErrorCode.INVALID_APPLICATION));

        if (appliedStudy.getParticipants() > 0) {
            appliedStudy.removeParticipant();
        } else {
            log.warn("스터디 ID {}: 지원자 수가 이미 0이므로 감소 작업을 수행하지 않았습니다.", studyId);
        }
        studyApplicationRepository.removeStudyApplication(studyApplication);
    }
}
