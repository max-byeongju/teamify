package matching.teamify.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Project;
import matching.teamify.dto.project.ProjectResponse;
import matching.teamify.dto.project.RecruitProjectResponse;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepository {

    private final EntityManager em;

    public Project save(Project project) {
        em.persist(project);
        return project;
    }

    public Optional<Project> findById(Long projectId) {
        Project project = em.find(Project.class, projectId);
        return Optional.ofNullable(project);
    }

    public Optional<Project> findByIdWithLock(Long projectId) {
        Project project = em.find(Project.class, projectId, LockModeType.PESSIMISTIC_WRITE);
        return Optional.ofNullable(project);
    }

    public List<RecruitProjectResponse> findProjectsByMemberId(Long memberId) {
        return em.createQuery("select new matching.teamify.dto.project.RecruitProjectResponse(" +
                        "p.recruiting, p.title, p.id) " +
                        "from Project p " +
                        "where p.member.id = :memberId", RecruitProjectResponse.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // 모든 프로젝트 목록을 페이징하여 조회
    public List<ProjectResponse> findAllProjectPaginated(int pageNumber, int pageSize) {

        String jpql = "select new matching.teamify.dto.project.ProjectResponse(" +
                "p.member.id, p.id, p.member.nickName, p.member.picture, p.title, p.projectDate, p.recruiting) " +
                "from Project p " +
                "order by p.projectDate desc";

        TypedQuery<ProjectResponse> query = em.createQuery(jpql, ProjectResponse.class);

        int firstResult = pageNumber * pageSize;
        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    // 전체 프로젝트 개수를 조회 (페이징 UI 구성에 필요)
    public long countAll() {
        // COUNT 쿼리는 단순하게 작성
        // WHERE 조건이 있다면 동일하게 추가해야 함
        String countJpql = "select count(p.id) from Project p";
        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
        return countQuery.getSingleResult();
    }

    // 7일 내 생성된 모집 중인프로젝트 조회
    public List<ProjectResponse> findRecentProjects(int limit) {

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        return em.createQuery("select new matching.teamify.dto.project.ProjectResponse(" +
                        "p.member.id, p.id, p.member.nickName, p.member.picture, p.title, p.projectDate, p.recruiting) " +
                        "from Project p " +
                        "where p.recruiting = true and p.projectDate >= :sevenDaysAgo " +
                        "order by p.projectDate desc", ProjectResponse.class)
                .setParameter("sevenDaysAgo", sevenDaysAgo)
                .setMaxResults(limit)
                .getResultList();
    }

    public void delete(Project project) {
        em.remove(project);
    }
}
