package matching.teamify.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Member;
import matching.teamify.domain.Study;
import matching.teamify.domain.StudyApplication;
import matching.teamify.dto.apply.StudyApplicantResponse;
import matching.teamify.dto.apply.StudyApplicationResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyApplicationRepository {

    private final EntityManager em;

    public void save(StudyApplication studyApplication) {
        em.persist(studyApplication);
    }

    public List<StudyApplicationResponse> findAppliedStudyByMemberId(Long memberId) {
        return em.createQuery("select new matching.teamify.dto.apply.StudyApplicationResponse(" +
                        "a.member.id, a.study.title, a.study.recruiting) " +
                        "from StudyApplication a " +
                        "where a.member.id = :memberId", StudyApplicationResponse.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<StudyApplicantResponse> findApplyMemberByStudyId(Long studyId) {
        return em.createQuery("select new matching.teamify.dto.apply.StudyApplicantResponse(" +
                        "a.member.id, a.member.picture, a.member.nickName) " +
                        "from StudyApplication a " +
                        "where a.study.id = :studyId", StudyApplicantResponse.class)
                .setParameter("studyId", studyId)
                .getResultList();
    }

    public Optional<StudyApplication> findByMemberIdAndStudyId(Long memberId, Long studyId) {
        return em.createQuery("select a from StudyApplication a where a.member.id = :memberId and a.study.id = :studyId", StudyApplication.class)
                .setParameter("memberId", memberId)
                .setParameter("studyId", studyId)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByMemberAndStudyId(Long memberId, Long studyId) {
        String jpql = "SELECT count(a) from StudyApplication a " +
                        "where a.member.id = :memberId and a.study.id = :studyId ";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("memberId", memberId)
                .setParameter("studyId", studyId)
                .getSingleResult();

        return count > 0;
    }

    public void removeStudyApplication(StudyApplication application) {
        em.remove(application);
    }
}
