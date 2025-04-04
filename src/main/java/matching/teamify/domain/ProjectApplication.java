package matching.teamify.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProjectApplication {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private ProjectRole role;

    @Lob
    private String applyNote;

    @Enumerated(value = EnumType.STRING)
    private ApplyStatus status = ApplyStatus.PENDING;

    public ProjectApplication(Project project, Member member, String applyNote, ProjectRole role) {
        this.project = project;
        this.member = member;
        this.applyNote = applyNote;
        this.role = role;
    }

    public void changeStatus(ApplyStatus status) {
        this.status = status;
    }


}
