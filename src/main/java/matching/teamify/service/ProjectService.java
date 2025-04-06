package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import matching.teamify.domain.Project;
import matching.teamify.dto.page.PageResponse;
import matching.teamify.dto.project.ProjectDetailResponse;
import matching.teamify.dto.project.ProjectRequest;
import matching.teamify.dto.project.ProjectResponse;
import matching.teamify.dto.project.RecruitProjectResponse;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public Long recruit(ProjectRequest projectRequest, Long memberId) {
        Member recruitMember = memberRepository.findById(memberId);
        Project project = convertToProject(projectRequest);

        project.createProject(recruitMember);
        Project savedProject = projectRepository.save(project);

        return savedProject.getId();
    }

    @Transactional
    public PageResponse<ProjectResponse> findProjectsPaginated(int page, int size, Long memberId) {
        List<ProjectResponse> content = projectRepository.findAllProjectPaginated(page, size);
        long totalElements = projectRepository.countAll();


        return new PageResponse<>(content, totalElements, page, size);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findRecentProjects(Long memberId) {
        return projectRepository.findRecentProjects(10);
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse findOneProject(Long memberId, Long projectId) {
        Project project = projectRepository.findById(projectId);
        return convertToProjectDetailResponse(project);
    }

    @Transactional(readOnly = true)
    public List<RecruitProjectResponse> findRecruitProjects(Long memberId) {
        return projectRepository.findProjectsByMemberId(memberId);
    }

    @Transactional
    public void updateProject(Long projectId, ProjectRequest projectRequest) {
        Project project = projectRepository.findById(projectId);
        project.updateProject(projectRequest);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId);
        projectRepository.delete(project);
    }

    @Transactional
    public void recruitingEnd(Long projectId) {
        Project project = projectRepository.findById(projectId);
        if (!project.isRecruiting()) {
            throw new RuntimeException("이미 마감된 프로젝트 입니다.");
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
                .projectDate(LocalDate.now())
                .build();
    }

    public ProjectDetailResponse convertToProjectDetailResponse(Project project) {
        return ProjectDetailResponse.builder()
                .title(project.getTitle())
                .field(project.getField())
                .techStack(project.getTechStack())
                .recruitNumber(project.getRecruitNumber())
                .frontendNumber(project.getFrontendNumber())
                .backendNumber(project.getBackendNumber())
                .designerNumber(project.getDesignerNumber())
                .content(project.getContent())
                .localDate(project.getProjectDate())
                .nickName(project.getMember().getNickName())
                .recruiting(project.isRecruiting())
                .build();
    }
}
