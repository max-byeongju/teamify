package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import matching.teamify.domain.Study;
import matching.teamify.dto.page.PageResponse;
import matching.teamify.dto.study.RecruitStudyResponse;
import matching.teamify.dto.study.StudyDetailResponse;
import matching.teamify.dto.study.StudyRequest;
import matching.teamify.dto.study.StudyResponse;
import matching.teamify.exception.common.EntityNotFoundException;
import matching.teamify.exception.study.StudyAlreadyClosedException;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;

    @Transactional
    public Long recruit(StudyRequest studyRequest, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        Study study = convertToStudy(studyRequest);

        study.createStudy(member);
        Study savedStudy = studyRepository.save(study);

        return savedStudy.getId();
    }

    @Transactional
    public PageResponse<StudyResponse> findStudiesPaginated(int page, int size, Long memberId) {
        List<StudyResponse> content = studyRepository.findAllStudyPaginated(page, size);
        long totalElements = studyRepository.countAll();


        return new PageResponse<>(content, totalElements, page, size);
    }

    @Transactional(readOnly = true)
    public List<StudyResponse> findRecentStudies(Long memberId) {
        return studyRepository.findRecentStudies(10);
    }

    @Transactional
    public StudyDetailResponse findOneStudy(Long memberId, Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new EntityNotFoundException("Study", studyId));
        return convertToStudyDetailResponse(study);
    }

    @Transactional(readOnly = true)
    public List<RecruitStudyResponse> findRecruitStudies(Long memberId) {
        return studyRepository.findStudiesByMemberId(memberId);
    }

    @Transactional
    public void updateStudy(Long studyId, StudyRequest studyRequest) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new EntityNotFoundException("Study", studyId));
        study.updateStudy(studyRequest);
    }

    @Transactional
    public void deleteStudy(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new EntityNotFoundException("Study", studyId));
        studyRepository.delete(study);
    }

    @Transactional
    public void recruitingEnd(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new EntityNotFoundException("Study", studyId));
        if (!study.isRecruiting()) {
            throw new StudyAlreadyClosedException("이미 마감된 스터디입니다.");
        }
        study.closeRecruitProject();
    }

    public Study convertToStudy(StudyRequest studyRequest) {
        return Study.builder()
                .title(studyRequest.getTitle())
                .recruitNumber(studyRequest.getRecruitNumber())
                .content(studyRequest.getContent())
                .studyDate(LocalDate.now())
                .build();
    }

    public StudyDetailResponse convertToStudyDetailResponse(Study study) {
        return StudyDetailResponse.builder()
                .title(study.getTitle())
                .recruitNumber(study.getRecruitNumber())
                .content(study.getContent())
                .studyDate(study.getStudyDate())
                .nickName(study.getMember().getNickName())
                .build();
    }
}
