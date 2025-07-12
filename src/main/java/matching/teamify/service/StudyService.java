package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.FavoriteStudy;
import matching.teamify.domain.Member;
import matching.teamify.domain.Study;
import matching.teamify.dto.page.PageResponse;
import matching.teamify.dto.study.RecruitStudyResponse;
import matching.teamify.dto.study.StudyDetailResponse;
import matching.teamify.dto.study.StudyRequest;
import matching.teamify.dto.study.StudyResponse;
import matching.teamify.common.exception.ErrorCode;
import matching.teamify.common.exception.TeamifyException;
import matching.teamify.repository.FavoriteRepository;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final S3ImageService s3ImageService;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public Long recruit(StudyRequest studyRequest, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Study study = convertToStudy(studyRequest);

        study.createStudy(member);
        Study savedStudy = studyRepository.save(study);

        return savedStudy.getId();
    }

    @Transactional(readOnly = true)
    public PageResponse<StudyResponse> findStudiesPaginated(int page, int size, Long memberId) {
        List<StudyResponse> content = studyRepository.findAllStudyPaginated(page, size);
        long totalElements = studyRepository.countAll();

        Set<Long> favoriteStudyIds = Collections.emptySet();
        if (memberId != null && !content.isEmpty()) {
            List<Long> favoriteList = favoriteRepository.findFavoriteStudyIds(memberId);
            favoriteStudyIds = new HashSet<>(favoriteList);
        }

        for (StudyResponse study : content) {
            String s3Key = study.getImageUrl();
            study.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
            study.setFavorite(favoriteStudyIds.contains(study.getStudyId()));
        }
        return new PageResponse<>(content, totalElements, page, size);
    }

    @Transactional(readOnly = true)
    public List<StudyResponse> findRecentStudies(Long memberId) {
        List<StudyResponse> studies = studyRepository.findRecentStudies(10);
        List<Long> favoriteList = favoriteRepository.findFavoriteStudyIds(memberId);
        HashSet<Long> favoriteStudyIds = new HashSet<>(favoriteList);

        for (StudyResponse study : studies) {
            String s3Key = study.getImageUrl();
            study.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
            study.setFavorite(favoriteStudyIds.contains(study.getStudyId()));
        }
        return studies;
    }

    @Transactional(readOnly = true)
    public StudyDetailResponse findOneStudy(Long memberId, Long studyId) {
        StudyDetailResponse studyDetailResponse = studyRepository.findStudyDetailDtoById(studyId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Optional<FavoriteStudy> favoriteStudy = favoriteRepository.findByMemberIdAndStudyId(memberId, studyId);

        boolean isFavorite = favoriteStudy.isPresent();
        studyDetailResponse.setFavorite(isFavorite);

        String s3Key = studyDetailResponse.getS3Key();
        studyDetailResponse.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
        return studyDetailResponse;
    }

    @Transactional(readOnly = true)
    public List<RecruitStudyResponse> findRecruitStudies(Long memberId) {
        return studyRepository.findStudiesByMemberId(memberId);
    }

    @Transactional
    public void updateStudy(Long studyId, StudyRequest studyRequest) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        study.updateStudy(studyRequest);
    }

    @Transactional
    public void deleteStudy(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        studyRepository.delete(study);
    }

    @Transactional
    public void recruitingEnd(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        if (!study.isRecruiting()) {
            throw new TeamifyException(ErrorCode.RECRUITMENT_CLOSED);
        }
        study.closeRecruitStudy();
    }

    public Study convertToStudy(StudyRequest studyRequest) {
        return Study.builder()
                .title(studyRequest.getTitle())
                .recruitNumber(studyRequest.getRecruitNumber())
                .content(studyRequest.getContent())
                .build();
    }
}
