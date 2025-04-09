package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.*;
import matching.teamify.dto.project.ProjectResponse;
import matching.teamify.dto.study.StudyResponse;
import matching.teamify.exception.common.EntityNotFoundException;
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

    @Transactional
    public void addFavoriteProject(Long memberId, Long projectId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        Project project = projectRepository.findById(projectId);
        FavoriteProject favoriteProject = new FavoriteProject(member, project);
        favoriteRepository.saveFavoriteProject(favoriteProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findFavoriteProjects(Long memberId) {
        List<FavoriteProject> favoriteProjects = favoriteRepository.findFavoriteProjects(memberId);
        return favoriteProjects.stream()
                .map(favoriteProject -> {
                    Project project = favoriteProject.getProject();
                    return new ProjectResponse(
                            project.getId(),
                            project.getMember().getNickName(),
                            project.getTitle(),
                            project.getProjectDate(),
                            project.isRecruiting(),
                            true
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelFavoriteProject(Long memberId, Long projectId) {
        FavoriteProject favoriteProject = favoriteRepository.findByMemberIdAndProjectId(memberId, projectId).orElseThrow(() -> new RuntimeException("관심 프로젝트를 찾을 수 없습니다"));
        favoriteRepository.deleteFavoriteProject(favoriteProject);
    }

    @Transactional
    public void addFavoriteStudy(Long memberId, Long studyId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        Study study = studyRepository.findById(studyId);
        FavoriteStudy favoriteStudy = new FavoriteStudy(member, study);
        favoriteRepository.saveFavoriteStudy(favoriteStudy);
    }

    @Transactional(readOnly = true)
    public List<StudyResponse> findFavoriteStudies(Long memberId) {
        List<FavoriteStudy> favoriteStudies = favoriteRepository.findFavoriteStudies(memberId);
        return favoriteStudies.stream()
                .map(favoriteStudy -> {
                    Study study = favoriteStudy.getStudy();
                    return new StudyResponse(
                            study.getId(),
                            study.getMember().getNickName(),
                            study.getTitle(),
                            study.getStudyDate(),
                            study.isRecruiting(),
                            true
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelFavoriteStudy(Long memberId, Long studyId) {
        FavoriteStudy favoriteStudy = favoriteRepository.findByMemberIdAndStudyId(memberId, studyId).orElseThrow(() -> new RuntimeException("관심 스터디를 찾을 수 없습니다."));
        favoriteRepository.deleteFavoriteStudy(favoriteStudy);
    }
}
