package matching.teamify.controller;

import lombok.RequiredArgsConstructor;
import matching.teamify.config.auth.Login;
import matching.teamify.dto.apply.StudyApplicantResponse;
import matching.teamify.dto.apply.StudyApplicationResponse;
import matching.teamify.service.StudyApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudyApplicationController {

    private final StudyApplicationService studyApplicationService;

    @PostMapping("/studies/{studyId}/applications")
    public ResponseEntity<Void> applyToStudy(@PathVariable Long studyId, @Login Long memberId) {
        studyApplicationService.applyToStudy(studyId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/studies/applications/mine")
    public ResponseEntity<List<StudyApplicationResponse>> findMyStudyApplications(@Login Long memberId) {
        return ResponseEntity.ok(studyApplicationService.findAppliedStudies(memberId));
    }

    @GetMapping("/studies/{studyId}/applicants")
    public ResponseEntity<List<StudyApplicantResponse>> findApplicantsForStudy(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyApplicationService.findApplicantsForStudy(studyId));
    }

    @DeleteMapping("/studies/{studyId}/applications")
    public ResponseEntity<Void> deleteMyStudyApplication(@PathVariable Long studyId, @Login Long memberId) {
        studyApplicationService.cancelApply(memberId, studyId);
        return ResponseEntity.noContent().build();
    }
}
