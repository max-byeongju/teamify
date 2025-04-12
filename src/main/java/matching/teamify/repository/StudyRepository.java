package matching.teamify.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Study;
import matching.teamify.dto.study.RecruitStudyResponse;
import matching.teamify.dto.study.StudyResponse;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyRepository {

    private final EntityManager em;

    public Study save(Study study) {
        em.persist(study);
        return study;
    }

    public Optional<Study> findById(Long studyId) {
        Study study = em.find(Study.class, studyId);
        return Optional.ofNullable(study);
    }

    public Optional<Study> findByIdWithLock(Long studyId) {
        Study study = em.find(Study.class, studyId, LockModeType.PESSIMISTIC_WRITE);
        return Optional.ofNullable(study);
    }

    public List<RecruitStudyResponse> findStudiesByMemberId(Long memberId) {
        return em.createQuery("select new matching.teamify.dto.study.RecruitStudyResponse(" +
                        "s.recruiting, s.title, s.id) " +
                        "from Study s " +
                        "where s.member.id = :memberId", RecruitStudyResponse.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // 모든 스터디 목록을 페이징하여 조회
    public List<StudyResponse> findAllStudyPaginated(int pageNumber, int pageSize) {
        String jpql = "select new matching.teamify.dto.study.StudyResponse(" +
                "s.member.id, s.id, s.member.nickName, s.member.picture, s.title, s.createdDate, s.recruiting) " +
                "from Study s " +
                "order by s.createdDate desc";

        TypedQuery<StudyResponse> query = em.createQuery(jpql, StudyResponse.class);

        int firstResult = pageNumber * pageSize;
        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    // 전체 스터디 개수를 조회 (페이징 UI 구성에 필요)
    public long countAll() {
        String countJpql = "select count(s.id) from Study s";
        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
        return countQuery.getSingleResult();
    }

    // 7일 내 생성된 모집중인 스터디 조회
    public List<StudyResponse> findRecentStudies(int limit) {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        return em.createQuery("select new matching.teamify.dto.study.StudyResponse(" +
                        "s.member.id, s.id, s.member.nickName, s.member.picture, s.title, s.createdDate, s.recruiting) " +
                        "from Study s " +
                        "where s.recruiting = true and s.createdDate >= :sevenDaysAgo " +
                        "order by s.createdDate desc", StudyResponse.class)
                .setParameter("sevenDaysAgo", sevenDaysAgo)
                .setMaxResults(limit)
                .getResultList();
    }

    public void delete(Study study) {
        em.remove(study);
    }
}
