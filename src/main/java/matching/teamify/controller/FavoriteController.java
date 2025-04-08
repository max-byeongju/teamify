package matching.teamify.controller;

import lombok.RequiredArgsConstructor;
import matching.teamify.config.auth.Login;
import matching.teamify.dto.project.ProjectResponse;
import matching.teamify.dto.study.StudyResponse;
import matching.teamify.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/projects/{projectId}/like")
    public ResponseEntity<Void> likeProject(@PathVariable Long projectId, @Login Long memberId) {
        favoriteService.addFavoriteProject(memberId, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/projects/{projectId}/dislike")
    public ResponseEntity<Void> dislikeProject(@PathVariable Long projectId, @Login Long memberId) {
        favoriteService.cancelFavoriteProject(memberId, projectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/like")
    public ResponseEntity<List<ProjectResponse>> favoriteProjects(@Login Long memberId) {
        return ResponseEntity.ok(favoriteService.findFavoriteProjects(memberId));
    }

    @PostMapping("/studies/{studyId}/like")
    public ResponseEntity<Void> likeStudy(@PathVariable Long studyId, @Login Long memberId) {
        favoriteService.addFavoriteStudy(memberId, studyId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/studies/{studyId}/dislike")
    public ResponseEntity<Void> dislikeStudy(@PathVariable Long studyId, @Login Long memberId) {
        favoriteService.cancelFavoriteStudy(memberId, studyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/studies/like")
    public ResponseEntity<List<StudyResponse>> favoriteStudies(@Login Long memberId) {
        return ResponseEntity.ok(favoriteService.findFavoriteStudies(memberId));
    }
}
