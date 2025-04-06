package matching.teamify.controller;

import lombok.RequiredArgsConstructor;
import matching.teamify.config.auth.Login;
import matching.teamify.dto.page.PageResponse;
import matching.teamify.dto.study.RecruitStudyResponse;
import matching.teamify.dto.study.StudyDetailResponse;
import matching.teamify.dto.study.StudyRequest;
import matching.teamify.dto.study.StudyResponse;
import matching.teamify.service.StudyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/studies")
    public ResponseEntity<Void> createStudy(@RequestBody StudyRequest studyRequest, @Login Long memberId) {
        Long studyId = studyService.recruit(studyRequest, memberId);
        return ResponseEntity.created(URI.create("/studies/" + studyId)).build();
    }

    @GetMapping("/studies")
    public ResponseEntity<PageResponse<StudyResponse>> getStudies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @Login Long memberId) {
        return ResponseEntity.ok(studyService.findStudiesPaginated(page, size, memberId));
    }

    @GetMapping("/studies/recent")
    public ResponseEntity<List<StudyResponse>> showRecentStudies(@Login Long memberId) {
        return ResponseEntity.ok(studyService.findRecentStudies(memberId));
    }

    @GetMapping("/studies/recruit")
    public ResponseEntity<List<RecruitStudyResponse>> getRecruitStudies(@Login Long memberId) {
        return ResponseEntity.ok(studyService.findRecruitStudies(memberId));
    }

    @GetMapping("/studies/{studyId}")
    public ResponseEntity<StudyDetailResponse> getStudyById(@PathVariable Long studyId, @Login Long memberId) {
        return ResponseEntity.ok(studyService.findOneStudy(memberId, studyId));
    }

    @PutMapping("/studies/{studyId}")
    public ResponseEntity<Void> updateStudy(@PathVariable Long studyId, @RequestBody StudyRequest studyRequest) {
        studyService.updateStudy(studyId, studyRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/studies/{studyId}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long studyId) {
        studyService.deleteStudy(studyId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/studies/{studyId}/recruiting")
    public ResponseEntity<Void> recruitingEnd(@PathVariable Long studyId) {
        studyService.recruitingEnd(studyId);
        return ResponseEntity.noContent().build();
    }
}
