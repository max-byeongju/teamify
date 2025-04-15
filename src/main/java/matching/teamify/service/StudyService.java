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
import matching.teamify.exception.common.EntityNotFoundException;
import matching.teamify.exception.study.StudyAlreadyClosedException;
import matching.teamify.repository.FavoriteRepository;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final S3ImageService s3ImageService;
    private final FavoriteRepository favoriteRepository;

    @Value("${DEFAULT_PROFILE_IMAGE_URL}")
    private String defaultProfileImageUrl;

    @Transactional
    public Long recruit(StudyRequest studyRequest, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member", memberId));
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
            study.setFavorite(favoriteStudyIds.contains(study.getStudyId()));
            String s3Key = study.getImageUrl();
            if (s3Key == null || s3Key.trim().isEmpty()) {
                study.setImageUrl(defaultProfileImageUrl);
            } else {
                study.setImageUrl(s3ImageService.getImageUrl(s3Key));
            }
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
            if (s3Key == null || s3Key.trim().isEmpty()) {
                study.setImageUrl(defaultProfileImageUrl);
            } else {
                study.setImageUrl(s3ImageService.getImageUrl(s3Key));
            }
            study.setFavorite(favoriteStudyIds.contains(study.getStudyId()));
        }
        return studies;
    }

    @Transactional(readOnly = true)
    public StudyDetailResponse findOneStudy(Long memberId, Long studyId) {
        StudyDetailResponse studyDetailResponse = studyRepository.findStudyDetailDtoById(studyId).orElseThrow(() -> new EntityNotFoundException("Study", studyId));
        Optional<FavoriteStudy> favoriteStudy = favoriteRepository.findByMemberIdAndStudyId(memberId, studyId);

        boolean isFavorite = favoriteStudy.isPresent();
        studyDetailResponse.setFavorite(isFavorite);

        String s3Key = studyDetailResponse.getS3Key();
        if (s3Key == null || s3Key.trim().isEmpty()) {
            studyDetailResponse.setImageUrl(defaultProfileImageUrl);
        } else {
            studyDetailResponse.setImageUrl(s3ImageService.getImageUrl(s3Key));
        }
        return studyDetailResponse;
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
