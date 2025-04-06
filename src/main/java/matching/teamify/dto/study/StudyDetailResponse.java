package matching.teamify.dto.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class StudyDetailResponse {

    private String title;
    private int recruitNumber;
    private String content;
    private LocalDate studyDate;
    private String nickName;
    private String imageUrl;
    private boolean favorite;

}
