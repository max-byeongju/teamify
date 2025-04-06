package matching.teamify.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProjectResponse {

    private Long memberId;
    private Long projectId;
    private String nickName;
    private String imageUrl;
    private String title;
    private LocalDate projectDate;
    private boolean recruiting;
    private boolean favorite;

    public ProjectResponse(Long projectId, String nickName, String title, LocalDate projectDate, boolean recruiting, boolean favorite) {
        this.projectId = projectId;
        this.nickName = nickName;
        this.title = title;
        this.projectDate = projectDate;
        this.recruiting = recruiting;
        this.favorite = favorite;
    }

    public ProjectResponse(Long memberId, Long projectId, String nickName, String imageUrl, String title, LocalDate projectDate, boolean recruiting) {
        this.memberId = memberId;
        this.projectId = projectId;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
        this.title = title;
        this.projectDate = projectDate;
        this.recruiting = recruiting;
    }

}
