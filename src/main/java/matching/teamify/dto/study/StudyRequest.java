package matching.teamify.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyRequest {

    private String title;
    private int recruitNumber;
    private String content;
}
