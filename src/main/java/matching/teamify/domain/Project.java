package matching.teamify.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import matching.teamify.dto.project.ProjectRequest;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Project {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String field;

    @Column(nullable = false)
    private String techStack;

    @Column(nullable = false)
    private int recruitNumber;

    @Column(nullable = false)
    private int frontendNumber;

    @Column(nullable = false)
    private int backendNumber;

    @Column(nullable = false)
    private int designerNumber;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(nullable = false)
    private boolean recruiting = true;

    @Column(nullable = false)
    private int frontApplyNumber = 0;

    @Column(nullable = false)
    private int backApplyNumber = 0;

    @Column(nullable = false)
    private int designApplyNumber = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Project(String title, String field, String techStack, int recruitNumber, int frontendNumber, int backendNumber, int designerNumber, String content) {
        this.title = title;
        this.field = field;
        this.techStack = techStack;
        this.recruitNumber = recruitNumber;
        this.frontendNumber = frontendNumber;
        this.backendNumber = backendNumber;
        this.designerNumber = designerNumber;
        this.content = content;
    }

    public void createProject(Member member) {
        this.member = member;
    }

    public void updateProject(ProjectRequest projectRequest) {
        this.title = projectRequest.getTitle();
        this.field = projectRequest.getField();
        this.techStack = projectRequest.getTechStack();
        this.recruitNumber = projectRequest.getRecruitNumber();
        this.frontendNumber = projectRequest.getFrontendNumber();
        this.backendNumber = projectRequest.getBackendNumber();
        this.designerNumber = projectRequest.getDesignerNumber();
        this.content = projectRequest.getContent();
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

    public void cancelFrontend() {
        this.frontApplyNumber--;
    }

    public void cancelBackend() {
        this.backApplyNumber--;
    }

    public void cancelDesigner() {
        this.designApplyNumber--;
    }
}