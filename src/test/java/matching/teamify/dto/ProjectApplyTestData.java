package matching.teamify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import matching.teamify.domain.Member;
import matching.teamify.domain.Project;

import java.util.List;

@Data
@AllArgsConstructor
public class ProjectApplyTestData {
    private final Project targetProject;
    private final List<Member> applicants;
}
