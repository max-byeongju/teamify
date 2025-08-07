package matching.teamify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import matching.teamify.domain.Member;
import matching.teamify.domain.Study;

import java.util.List;

@Data
@AllArgsConstructor
public class StudyApplyTestData {
    private final Study targetStudy;
    private final List<Member> applicants;
}
