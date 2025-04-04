package matching.teamify.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Project {

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String title;
    @Column
    private String field;
    @Column
    private String techStack;
    @Column
    private int recruitNumber;
    @Column
    private int frontendNumber;
    @Column
    private int backendNumber;
    @Column
    private int designerNumber;
    @Lob
    private String content;
    @Column
    private LocalDate projectDate;
    @Column
    private boolean recruiting = true;

    private int frontApplyNumber = 0;
    private int backApplyNumber = 0;
    private int designApplyNumber = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Project(String title, String field, String techStack, int recruitNumber, int frontendNumber, int backendNumber, int designerNumber, String content, LocalDate projectDate) {
        this.title = title;
        this.field = field;
        this.techStack = techStack;
        this.recruitNumber = recruitNumber;
        this.frontendNumber = frontendNumber;
        this.backendNumber = backendNumber;
        this.designerNumber = designerNumber;
        this.content = content;
        this.projectDate = projectDate;
    }

    public void deleteProject() {
        this.member = null;
    }

    public void createProject(Member member) {
        this.member = member;
    }

    public void updateProject(String title, String field, String techStack, int recruitNumber, int frontendNumber, int backendNumber, int designerNumber, String content) {
        this.title = title;
        this.field = field;
        this.techStack = techStack;
        this.recruitNumber = recruitNumber;
        this.frontendNumber = frontendNumber;
        this.backendNumber = backendNumber;
        this.designerNumber = designerNumber;
        this.content = content;
    }

    public void closeRecruitProject() {
        this.recruiting = false;
    }

    public int getMaxApplicationsForFrontend() {
        return frontendNumber * 3;
    }

    public int getMaxApplicationForBackend() {
        return backendNumber * 3;
    }

    public int getMaxApplicationForDesigner() {
        return designerNumber * 3;
    }

    public void recruitedFrontend() {
        this.frontApplyNumber++;
    }

    public void recruitedBackend() {
        this.backApplyNumber++;
    }

    public void recruitedDesigner() {
        this.designApplyNumber++;
    }

}