package matching.teamify.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyApplicantResponse {
    private Long memberId;
    private String imageUrl;
    private String nickName;
}
