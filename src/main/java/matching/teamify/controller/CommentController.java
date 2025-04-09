package matching.teamify.controller;

import lombok.RequiredArgsConstructor;
import matching.teamify.config.auth.Login;
import matching.teamify.dto.comment.CommentRequest;
import matching.teamify.dto.comment.CommentResponse;
import matching.teamify.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/projects/comment/{projectId}")
    public ResponseEntity<Void> createProjectComment(@PathVariable Long projectId, @Login Long memberId, @RequestBody CommentRequest commentRequest) {
        commentService.createProjectComment(projectId, memberId, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/projects/comment/{projectId}")
    public ResponseEntity<List<CommentResponse>> getProjectComments(@PathVariable Long projectId) {
        return ResponseEntity.ok(commentService.findProjectComments(projectId));
    }

    @PostMapping("/studies/comment/{studyId}")
    public ResponseEntity<Void> createStudyComment(@PathVariable Long studyId, @Login Long memberId,@RequestBody CommentRequest commentRequest) {
        commentService.createStudyComment(studyId, memberId, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/studies/comment/{studyId}")
    public ResponseEntity<List<CommentResponse>> getStudyComments(@PathVariable Long studyId) {
        return ResponseEntity.ok(commentService.findStudyComments(studyId));
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @Login Long memberId) {
        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.noContent().build();
    }
}
