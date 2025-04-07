package matching.teamify.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import matching.teamify.domain.ProjectRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectApplicationRequest {

    private String application;
    private ProjectRole role;

}
