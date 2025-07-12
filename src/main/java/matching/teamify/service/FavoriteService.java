package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.*;
import matching.teamify.dto.project.ProjectResponse;
import matching.teamify.dto.study.StudyResponse;
import matching.teamify.common.exception.ErrorCode;
import matching.teamify.common.exception.TeamifyException;
import matching.teamify.repository.FavoriteRepository;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectRepository;
import matching.teamify.repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final StudyRepository studyRepository;
    private final FavoriteRepository favoriteRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public void addFavoriteProject(Long memberId, Long projectId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        FavoriteProject favoriteProject = new FavoriteProject(member, project);
        favoriteRepository.saveFavoriteProject(favoriteProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findFavoriteProjects(Long memberId) {
        List<FavoriteProject> favoriteProjects = favoriteRepository.findFavoriteProjects(memberId);
        return favoriteProjects.stream()
                .map(favoriteProject -> {
                    Project project = favoriteProject.getProject();
                    Member projectMember = project.getMember();
                    String s3Key = projectMember.getPicture();

                    String imageUrl = s3ImageService.generatePresignedUrl(s3Key);

                    return new ProjectResponse(
                            projectMember.getId(),
                            project.getId(),
                            projectMember.getNickName(),
                            imageUrl,
                            project.getTitle(),
                            project.getCreatedDate(),
                            project.isRecruiting(),
                            true
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelFavoriteProject(Long memberId, Long projectId) {
        FavoriteProject favoriteProject = favoriteRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        favoriteRepository.deleteFavoriteProject(favoriteProject);
    }

    @Transactional
    public void addFavoriteStudy(Long memberId, Long studyId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        FavoriteStudy favoriteStudy = new FavoriteStudy(member, study);
        favoriteRepository.saveFavoriteStudy(favoriteStudy);
    }

    @Transactional(readOnly = true)
    public List<StudyResponse> findFavoriteStudies(Long memberId) {
        List<FavoriteStudy> favoriteStudies = favoriteRepository.findFavoriteStudies(memberId);
        return favoriteStudies.stream()
                .map(favoriteStudy -> {
                    Study study = favoriteStudy.getStudy();
                    Member studyMember = study.getMember();
                    String s3Key = studyMember.getPicture();

                    String imageUrl = s3ImageService.generatePresignedUrl(s3Key);

                    return new StudyResponse(
                            studyMember.getId(),
                            study.getId(),
                            studyMember.getNickName(),
                            imageUrl,
                            study.getTitle(),
                            study.getCreatedDate(),
                            study.isRecruiting(),
                            true
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelFavoriteStudy(Long memberId, Long studyId) {
        FavoriteStudy favoriteStudy = favoriteRepository.findByMemberIdAndStudyId(memberId, studyId)
                .orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        favoriteRepository.deleteFavoriteStudy(favoriteStudy);
    }
}
