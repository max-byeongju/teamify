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
    private String s3Key;
    private boolean favorite;

    public StudyDetailResponse(String title, int recruitNumber, String content, LocalDate studyDate, String nickName, String s3Key) {
        this.title = title;
        this.recruitNumber = recruitNumber;
        this.content = content;
        this.studyDate = studyDate;
        this.nickName = nickName;
        this.s3Key = s3Key;
    }
}
