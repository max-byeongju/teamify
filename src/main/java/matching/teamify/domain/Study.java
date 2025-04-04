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
public class Study {

    @Id @GeneratedValue
    private Long id;
    @Column
    private String title;
    @Column
    private int recruitNumber;
    @Column
    @Lob
    private String content;
    @Column
    private LocalDate studyDate;
    @Column
    private boolean recruiting = true;
    @Column
    private int participants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Study(String title, int recruitNumber, String content, LocalDate studyDate) {
        this.title = title;
        this.recruitNumber = recruitNumber;
        this.content = content;
        this.studyDate = studyDate;
    }

    public void createStudy(Member member) {
        this.member = member;
    }

    public void updateStudy(String _title, int _recruitNumber, String _content) {
        this.title = _title;
        this.recruitNumber = _recruitNumber;
        this.content = _content;
    }

    public void deleteStudy() {
        this.member = null;
    }

    public void closeRecruitProject() {
        this.recruiting = false;
    }

    public void participantAdd() {
        this.participants++;
    }

}
