package matching.teamify.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.config.auth.Login;
import matching.teamify.dto.page.PageResponse;
import matching.teamify.dto.project.ProjectDetailResponse;
import matching.teamify.dto.project.ProjectRequest;
import matching.teamify.dto.project.ProjectResponse;
import matching.teamify.dto.project.RecruitProjectResponse;
import matching.teamify.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/projects")
    public ResponseEntity<Void> createProject(@RequestBody ProjectRequest projectRequest, @Login Long memberId) {
        Long projectId = projectService.recruit(projectRequest, memberId);
        return ResponseEntity.created(URI.create("/projects/" + projectId)).build();
    }

    @GetMapping("/projects")
    public ResponseEntity<PageResponse<ProjectResponse>> getProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @Login Long memberId) {
        return ResponseEntity.ok(projectService.findProjectsPaginated(page, size, memberId));
    }

    @GetMapping("/projects/recent")
    public ResponseEntity<List<ProjectResponse>> showRecentProjects(@Login Long memberId) {
        return ResponseEntity.ok(projectService.findRecentProjects(memberId));
    }

    @GetMapping("/projects/recruit")
    public ResponseEntity<List<RecruitProjectResponse>> getRecruitProjects(@Login Long memberId) {
        return ResponseEntity.ok(projectService.findRecruitProjects(memberId));
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(@PathVariable Long projectId, @Login Long memberId) {
        return ResponseEntity.ok(projectService.findOneProject(memberId, projectId));
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<Void> updateProject(@PathVariable Long projectId, @RequestBody ProjectRequest projectRequest) {
        projectService.updateProject(projectId, projectRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/projects/{projectId}/recruiting")
    public ResponseEntity<Void> recruitingEnd(@PathVariable Long projectId) {
        projectService.recruitingEnd(projectId);
        return ResponseEntity.noContent().build();
    }
}
