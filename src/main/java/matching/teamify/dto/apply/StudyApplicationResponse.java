package matching.teamify.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyApplicationResponse {

    private Long studyId;
    private String studyName;
    private boolean recruiting;

}
