package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.FavoriteProject;
import matching.teamify.domain.Member;
import matching.teamify.domain.Project;
import matching.teamify.dto.page.PageResponse;
import matching.teamify.dto.project.ProjectDetailResponse;
import matching.teamify.dto.project.ProjectRequest;
import matching.teamify.dto.project.ProjectResponse;
import matching.teamify.dto.project.RecruitProjectResponse;
import matching.teamify.exception.common.EntityNotFoundException;
import matching.teamify.exception.project.ProjectAlreadyClosedException;
import matching.teamify.repository.FavoriteRepository;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final FavoriteRepository favoriteRepository;
    private final S3ImageService s3ImageService;

    @Value("${app.default-profile-image-url}")
    private String defaultProfileImageUrl;

    @Transactional
    public Long recruit(ProjectRequest projectRequest, Long memberId) {
        Member recruitMember = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        Project project = convertToProject(projectRequest);

        project.createProject(recruitMember);
        Project savedProject = projectRepository.save(project);

        return savedProject.getId();
    }

    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> findProjectsPaginated(int page, int size, Long memberId) {
        List<ProjectResponse> content = projectRepository.findAllProjectPaginated(page, size);
        long totalElements = projectRepository.countAll();

        Set<Long> favoriteProjectIds = Collections.emptySet();
        if (memberId != null && !content.isEmpty()) {
            List<Long> favoriteList = favoriteRepository.findFavoriteProjectIds(memberId);
            favoriteProjectIds = new HashSet<>(favoriteList);
        }

        for (ProjectResponse project : content) {
            project.setFavorite(favoriteProjectIds.contains(project.getProjectId()));
            String s3Key = project.getImageUrl();
            if (s3Key == null || s3Key.trim().isEmpty()) {
                project.setImageUrl(defaultProfileImageUrl);
            } else {
                project.setImageUrl(s3ImageService.getImageUrl(s3Key));
            }
        }
        return new PageResponse<>(content, totalElements, page, size);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findRecentProjects(Long memberId) {
        List<ProjectResponse> projects = projectRepository.findRecentProjects(10);
        List<Long> favoriteList = favoriteRepository.findFavoriteProjectIds(memberId);
        HashSet<Long> favoriteProjectIds = new HashSet<>(favoriteList);

        for (ProjectResponse project : projects) {
            String s3Key = project.getImageUrl();
            if (s3Key == null || s3Key.trim().isEmpty()) {
                project.setImageUrl(defaultProfileImageUrl);
            } else {
                project.setImageUrl(s3ImageService.getImageUrl(s3Key));
            }
            project.setFavorite(favoriteProjectIds.contains(project.getProjectId()));
        }
        return projects;
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse findOneProject(Long memberId, Long projectId) {
        ProjectDetailResponse projectDetailResponse = projectRepository.findProjectDetailDtoById(projectId).orElseThrow(() -> new EntityNotFoundException("Project", projectId));
        Optional<FavoriteProject> favoriteProject = favoriteRepository.findByMemberIdAndProjectId(memberId, projectId);

        boolean isFavorite = favoriteProject.isPresent();
        projectDetailResponse.setFavorite(isFavorite);

        String s3Key = projectDetailResponse.getS3Key();
        if (s3Key == null || s3Key.trim().isEmpty()) {
            projectDetailResponse.setImageUrl(defaultProfileImageUrl);
        } else {
            projectDetailResponse.setImageUrl(s3ImageService.getImageUrl(s3Key));
        }
        return projectDetailResponse;
    }

    @Transactional(readOnly = true)
    public List<RecruitProjectResponse> findRecruitProjects(Long memberId) {
        return projectRepository.findProjectsByMemberId(memberId);
    }

    @Transactional
    public void updateProject(Long projectId, ProjectRequest projectRequest) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project", projectId));
        project.updateProject(projectRequest);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project", projectId));
        projectRepository.delete(project);
    }

    @Transactional
    public void recruitingEnd(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project", projectId));
        if (!project.isRecruiting()) {
            throw new ProjectAlreadyClosedException("이미 마감된 프로젝트 입니다.");
        }
        project.closeRecruitProject();
    }

    public Project convertToProject(ProjectRequest projectRequest) {
        return Project.builder()
                .title(projectRequest.getTitle())
                .field(projectRequest.getField())
                .techStack(projectRequest.getTechStack())
                .recruitNumber(projectRequest.getRecruitNumber())
                .frontendNumber(projectRequest.getFrontendNumber())
                .backendNumber(projectRequest.getBackendNumber())
                .designerNumber(projectRequest.getDesignerNumber())
                .content(projectRequest.getContent())
                .build();
    }
}
