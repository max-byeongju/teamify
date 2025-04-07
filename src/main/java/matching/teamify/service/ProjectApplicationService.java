package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.domain.*;
import matching.teamify.dto.apply.ProjectApplicantResponse;
import matching.teamify.dto.apply.ProjectApplicationRequest;
import matching.teamify.dto.apply.ProjectApplicationResponse;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectApplicationRepository;
import matching.teamify.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectApplicationService {

    private final ProjectApplicationRepository projectApplicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

    @Value("${app.default-profile-image-url}")
    private String defaultProfileImageUrl;

    @Transactional
    public void applyToProject(Long projectId, Long memberId, ProjectApplicationRequest applicationRequest) {
        Project applyProject = projectRepository.findById(projectId);
        Member applyMember = memberRepository.findById(memberId);

        if (Objects.equals(memberId, applyProject.getMember().getId())) {
            throw new RuntimeException("본인의 프로젝트에는 지원할 수 없습니다.");
        }
        if (!applyProject.isRecruiting()) {
            throw new RuntimeException("이미 마감된 프로젝트입니다.");
        }
        switch (applicationRequest.getRole()) {
            case FRONTEND -> {
                if (applyProject.getMaxApplicationsForFrontend() > applyProject.getFrontApplyNumber()) {
                    applyProject.recruitedFrontend();
                } else {
                    throw new RuntimeException("프론트엔드 모집 인원이 가득 찼습니다.");
                }
            }
            case BACKEND -> {
                if (applyProject.getMaxApplicationForBackend() > applyProject.getBackApplyNumber()) {
                    applyProject.recruitedBackend();
                } else {
                    throw new RuntimeException("백엔드 모집 인원이 가득 찼습니다.");
                }
            }
            case DESIGNER -> {
                if (applyProject.getMaxApplicationForDesigner() > applyProject.getDesignApplyNumber()) {
                    applyProject.recruitedDesigner();
                } else {
                    throw new RuntimeException("디자이너 모집 인원이 가득 찼습니다.");
                }
            }
            default -> throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        projectApplicationRepository.save(applyProject, applyMember, applicationRequest.getApplication(), applicationRequest.getRole());
    }

    @Transactional(readOnly = true)
    public List<ProjectApplicationResponse> findAppliedProjects(Long memberId) {
        return projectApplicationRepository.findAppliedProjectByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<ProjectApplicantResponse> findApplicantsForProject(Long projectId) {
        List<ProjectApplicantResponse> projectApplicantResponseList = projectApplicationRepository.findApplyMemberByProjectId(projectId);
        for (ProjectApplicantResponse applyMember : projectApplicantResponseList) {
            String s3Key = applyMember.getImageUrl();
            if (s3Key == null || s3Key.trim().isEmpty()) {
                applyMember.setImageUrl(defaultProfileImageUrl);
            } else {
                String imageUrl = s3ImageService.getImageUrl(s3Key);
                applyMember.setImageUrl(imageUrl);
            }
        }
        return projectApplicantResponseList;
    }

    @Transactional
    public void approveProject(Long memberId, Long projectId) {
        ProjectApplication projectApplication = findAndValidatePendingApplication(memberId, projectId);
        projectApplication.changeStatus(ApplyStatus.APPROVED);
    }

    @Transactional
    public void denyProject(Long memberId, Long projectId) {
        ProjectApplication projectApplication = findAndValidatePendingApplication(memberId, projectId);
        projectApplication.changeStatus(ApplyStatus.REJECTED);
    }

    @Transactional
    public void cancelApply(Long memberId, Long projectId) {
        Project appliedProject = projectRepository.findById(projectId);
        ProjectApplication projectApplication = projectApplicationRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new RuntimeException("지원 정보를 찾을 수 없습니다."));
        switch (projectApplication.getRole()) {
            case FRONTEND -> {
                if (appliedProject.getFrontApplyNumber() > 0) {
                    appliedProject.cancelFrontend();
                } else {
                    log.warn("프로젝트 ID {}: 프론트엔드 지원자 수가 이미 0이므로 감소 작업을 수행하지 않았습니다.", projectId);
                }
            }
            case BACKEND -> {
                if (appliedProject.getBackApplyNumber() > 0) {
                    appliedProject.cancelBackend();
                } else {
                    log.warn("프로젝트 ID {}: 백엔드 지원자 수가 이미 0이므로 감소 작업을 수행하지 않았습니다.", projectId);
                }
            }
            case DESIGNER -> {
                if (appliedProject.getDesignApplyNumber() > 0) {
                    appliedProject.cancelDesigner();
                } else {
                    log.warn("프로젝트 ID {}: 디자이너 지원자 수가 이미 0이므로 감소 작업을 수행하지 않았습니다.", projectId);
                }
            }
            default -> throw new RuntimeException("역할이 잘못 설정되어 있습니다.");
        }
        projectApplicationRepository.removeProjectApplication(projectApplication);
    }

    private ProjectApplication findAndValidatePendingApplication(Long memberId, Long projectId) {
        ProjectApplication projectApplication = projectApplicationRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new RuntimeException("지원 정보를 찾을 수 없습니다."));
        if (projectApplication.getStatus() != ApplyStatus.PENDING) {
            if (projectApplication.getStatus() == ApplyStatus.APPROVED) {
                throw new RuntimeException("이미 승인된 멤버입니다.");
            } else {
                throw new RuntimeException("이미 거절된 멤버입니다.");
            }
        }
        return projectApplication;
    }
}
