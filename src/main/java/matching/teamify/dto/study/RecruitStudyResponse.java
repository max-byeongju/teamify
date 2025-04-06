package matching.teamify.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecruitStudyResponse {

    private boolean recruiting;
    private String title;
    private Long studyId;

}
