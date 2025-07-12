package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.domain.*;
import matching.teamify.dto.apply.ProjectApplicantResponse;
import matching.teamify.dto.apply.ProjectApplicationRequest;
import matching.teamify.dto.apply.ProjectApplicationResponse;
import matching.teamify.common.exception.ErrorCode;
import matching.teamify.common.exception.TeamifyException;
import matching.teamify.repository.MemberRepository;
import matching.teamify.repository.ProjectApplicationRepository;
import matching.teamify.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectApplicationService {

    private final ProjectApplicationRepository projectApplicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public void applyToProject(Long projectId, Long memberId, ProjectApplicationRequest applicationRequest) {
        Project applyProject = projectRepository.findByIdWithLock(projectId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        Member applyMember = memberRepository.findById(memberId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));

        Optional<ProjectApplication> existingApplication = projectApplicationRepository.findByMemberIdAndProjectId(memberId, projectId);
        if (existingApplication.isPresent()) {
            throw new TeamifyException(ErrorCode.APPLICATION_ALREADY_EXISTS);
        }

        if (Objects.equals(memberId, applyProject.getMember().getId())) {
            throw new TeamifyException(ErrorCode.CANNOT_APPLY_TO_OWN_RECRUITMENT);
        }
        if (!applyProject.isRecruiting()) {
            throw new TeamifyException(ErrorCode.RECRUITMENT_CLOSED);
        }
        ProjectRole role = applicationRequest.getRole();
        switch (role) {
            case FRONTEND -> {
                if (applyProject.getMaxApplicationsForFrontend() <= applyProject.getFrontApplyNumber()) {
                    throw new TeamifyException(ErrorCode.RECRUITMENT_FULL);
                }
                applyProject.recruitedFrontend();
            }
            case BACKEND -> {
                if (applyProject.getMaxApplicationForBackend() <= applyProject.getBackApplyNumber()) {
                    throw new TeamifyException(ErrorCode.RECRUITMENT_FULL);
                }
                applyProject.recruitedBackend();
            }
            case DESIGNER -> {
                if (applyProject.getMaxApplicationForDesigner() <= applyProject.getDesignApplyNumber()) {
                    throw new TeamifyException(ErrorCode.RECRUITMENT_FULL);
                }
                applyProject.recruitedDesigner();
            }
            default -> throw new TeamifyException(ErrorCode.INVALID_ROLE);
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
            applyMember.setImageUrl(s3ImageService.generatePresignedUrl(s3Key));
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
        Project appliedProject = projectRepository.findById(projectId).orElseThrow(() -> new TeamifyException(ErrorCode.ENTITY_NOT_FOUND));
        ProjectApplication projectApplication = projectApplicationRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new TeamifyException(ErrorCode.INVALID_APPLICATION));
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
            default -> {
                log.error("데이터베이스에 유효하지 않은 역할 정보가 저장되어 있습니다. Application ID: {}", projectApplication.getId());
                throw new TeamifyException(ErrorCode.UNEXPECTED_SERVER_ERROR);
            }
        }
        projectApplicationRepository.removeProjectApplication(projectApplication);
    }

    private ProjectApplication findAndValidatePendingApplication(Long memberId, Long projectId) {
        ProjectApplication projectApplication = projectApplicationRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new TeamifyException(ErrorCode.INVALID_APPLICATION));
        ApplyStatus status = projectApplication.getStatus();
        if (status != ApplyStatus.PENDING) {
            if (status == ApplyStatus.APPROVED) {
                throw new TeamifyException(ErrorCode.APPLICATION_ALREADY_APPROVED);
            } else {
                throw new TeamifyException(ErrorCode.APPLICATION_ALREADY_REJECTED);
            }
        }
        return projectApplication;
    }
}
