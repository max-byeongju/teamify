package matching.teamify.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ProjectDetailResponse {

    private String title;
    private String field;
    private String techStack;
    private int recruitNumber;
    private int frontendNumber;
    private int backendNumber;
    private int designerNumber;
    private String content;
    private LocalDate localDate;
    private String nickName;
    private String imageUrl;
    private boolean recruiting;
    private boolean favorite;

}
