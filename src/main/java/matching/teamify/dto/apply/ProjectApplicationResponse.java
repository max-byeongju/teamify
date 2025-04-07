package matching.teamify.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Data;
import matching.teamify.domain.ApplyStatus;

@Data
@AllArgsConstructor
public class ProjectApplicationResponse {

    private Long projectId;
    private String projectName;
    private ApplyStatus applyStatus;
    private boolean recruiting;

}
