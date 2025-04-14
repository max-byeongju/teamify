package matching.teamify.controller;

import lombok.RequiredArgsConstructor;
import matching.teamify.config.auth.Login;
import matching.teamify.dto.apply.ApplicationReferenceRequest;
import matching.teamify.dto.apply.ProjectApplicantResponse;
import matching.teamify.dto.apply.ProjectApplicationRequest;
import matching.teamify.dto.apply.ProjectApplicationResponse;
import matching.teamify.service.ProjectApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectApplicationController {

    private final ProjectApplicationService projectApplicationService;

    @PostMapping("/projects/{projectId}/applications")
    public ResponseEntity<Void> applyToProject(@PathVariable Long projectId, @RequestBody ProjectApplicationRequest projectApplicationRequest, @Login Long memberId) {
        projectApplicationService.applyToProject(projectId, memberId, projectApplicationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/projects/applications/mine")
    public ResponseEntity<List<ProjectApplicationResponse>> findMyProjectApplications(@Login Long memberId) {
        return ResponseEntity.ok(projectApplicationService.findAppliedProjects(memberId));
    }

    @GetMapping("/projects/{projectId}/applicants")
    public ResponseEntity<List<ProjectApplicantResponse>> findApplicantsForProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectApplicationService.findApplicantsForProject(projectId));
    }

    @PostMapping("/projects/applications/approve")
    public ResponseEntity<Void> approveApplication(@RequestBody ApplicationReferenceRequest request) {
        projectApplicationService.approveProject(request.getMemberId(), request.getProjectId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/projects/applications/deny")
    public ResponseEntity<Void> denyApplication(@RequestBody ApplicationReferenceRequest request) {
        projectApplicationService.denyProject(request.getMemberId(), request.getProjectId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/projects/{projectId}/applications")
    public ResponseEntity<Void> deleteMyProjectApplication(@PathVariable Long projectId, @Login Long memberId) {
        projectApplicationService.cancelApply(memberId, projectId);
        return ResponseEntity.noContent().build();
    }
}
