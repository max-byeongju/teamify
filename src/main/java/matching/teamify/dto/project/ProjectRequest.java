package matching.teamify.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    private String title;
    private String field;
    private String techStack;
    private int recruitNumber;
    private int frontendNumber;
    private int backendNumber;
    private int designerNumber;
    private String content;

}
