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
import matching.teamify.common.exception.ErrorCode;
import matching.teamify.common.exception.TeamifyException;
import matching.teamify.repository.FavoriteRepository;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final FavoriteRepository favoriteRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public Long recruit(ProjectRequest projectRequest, Long memberId) {
        Member recruitMember = memberRepository.findById(memberId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
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
            project.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
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
            project.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
            project.setFavorite(favoriteProjectIds.contains(project.getProjectId()));
        }
        return projects;
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse findOneProject(Long memberId, Long projectId) {
        ProjectDetailResponse projectDetailResponse = projectRepository.findProjectDetailDtoById(projectId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Optional<FavoriteProject> favoriteProject = favoriteRepository.findByMemberIdAndProjectId(memberId, projectId);

        boolean isFavorite = favoriteProject.isPresent();
        projectDetailResponse.setFavorite(isFavorite);

        String s3Key = projectDetailResponse.getS3Key();
        projectDetailResponse.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
        return projectDetailResponse;
    }

    @Transactional(readOnly = true)
    public List<RecruitProjectResponse> findRecruitProjects(Long memberId) {
        return projectRepository.findProjectsByMemberId(memberId);
    }

    @Transactional
    public void updateProject(Long projectId, ProjectRequest projectRequest) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        project.updateProject(projectRequest);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        projectRepository.delete(project);
    }

    @Transactional
    public void recruitingEnd(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        if (!project.isRecruiting()) {
            throw new TeamifyException(ErrorCode.RECRUITMENT_CLOSED);
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
