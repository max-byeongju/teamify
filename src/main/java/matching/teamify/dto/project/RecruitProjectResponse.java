package matching.teamify.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecruitProjectResponse {

    private boolean recruiting;
    private String title;
    private Long projectId;

}
