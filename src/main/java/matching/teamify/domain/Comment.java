package matching.teamify.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment {

    @Id @GeneratedValue
    private Long id;

    @Column
    private LocalDateTime localDateTime;

    @Column
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private CommentType type;

    @Builder
    public Comment(String name, String picture, LocalDateTime localDateTime, String comment, Project project, Study study, Member member, CommentType type) {
        this.name = name;
        this.picture = picture;
        this.localDateTime = localDateTime;
        this.comment = comment;
        this.project = project;
        this.study = study;
        this.member = member;
        this.type = type;
    }

}
