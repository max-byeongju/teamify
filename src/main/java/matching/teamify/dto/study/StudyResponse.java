package matching.teamify.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class StudyResponse {

    private Long memberId;
    private Long studyId;
    private String nickName;
    private String imageUrl;
    private String title;
    private LocalDate studyDate;
    private boolean recruiting;
    private boolean favorite;

    public StudyResponse(Long memberId, Long studyId, String nickName, String imageUrl, String title, LocalDate studyDate, boolean recruiting) {
        this.memberId = memberId;
        this.studyId = studyId;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
        this.title = title;
        this.studyDate = studyDate;
        this.recruiting = recruiting;
    }
}
