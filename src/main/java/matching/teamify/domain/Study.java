package matching.teamify.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import matching.teamify.dto.study.StudyRequest;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Study {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int recruitNumber;

    @Lob
    @Column(nullable = false,  columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(nullable = false)
    private boolean recruiting = true;

    @Column(nullable = false)
    private int participants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Study(String title, int recruitNumber, String content) {
        this.title = title;
        this.recruitNumber = recruitNumber;
        this.content = content;
    }

    public void createStudy(Member member) {
        this.member = member;
    }

    public void updateStudy(StudyRequest studyRequest) {
        this.title = studyRequest.getTitle();
        this.recruitNumber = studyRequest.getRecruitNumber();
        this.content = studyRequest.getContent();
    }

    public void closeRecruitProject() {
        this.recruiting = false;
    }

    public void addParticipant() {
        this.participants++;
    }

    public void removeParticipant() {
        this.participants--;
    }
}
