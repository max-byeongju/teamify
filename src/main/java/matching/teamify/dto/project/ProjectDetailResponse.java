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
    private String s3Key;
    private boolean favorite;

    public ProjectDetailResponse(String title, String field, String techStack, int recruitNumber, int frontendNumber, int backendNumber, int designerNumber, String content, LocalDate localDate, String nickName, boolean recruiting, String s3Key) {
        this.title = title;
        this.field = field;
        this.techStack = techStack;
        this.recruitNumber = recruitNumber;
        this.frontendNumber = frontendNumber;
        this.backendNumber = backendNumber;
        this.designerNumber = designerNumber;
        this.content = content;
        this.localDate = localDate;
        this.nickName = nickName;
        this.recruiting = recruiting;
        this.s3Key = s3Key;
    }
}
