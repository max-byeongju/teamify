package matching.teamify.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.domain.Member;
import matching.teamify.domain.Project;
import matching.teamify.domain.ProjectApplication;
import matching.teamify.domain.ProjectRole;
import matching.teamify.dto.apply.ProjectApplicantResponse;
import matching.teamify.dto.apply.ProjectApplicationResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProjectApplicationRepository {

    private final EntityManager em;

    public void save(Project project, Member member, String applyNote, ProjectRole role) {
        ProjectApplication projectApplication = new ProjectApplication(project, member, applyNote, role);
        em.persist(projectApplication);
    }

    public List<ProjectApplicationResponse> findAppliedProjectByMemberId(Long memberId) {
        return em.createQuery("select new matching.teamify.dto.apply.ProjectApplicationResponse(" +
                        "a.project.id, a.project.title, a.status, a.project.recruiting) " +
                        "from ProjectApplication a " +
                        "where a.member.id = :memberId", ProjectApplicationResponse.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<ProjectApplicantResponse> findApplyMemberByProjectId(Long projectId) {
        return em.createQuery("select new matching.teamify.dto.apply.ProjectApplicantResponse(" +
                        "m.id, a.status, m.picture, m.nickName, a.role, a.applyNote) " +
                        "from ProjectApplication a " +
                        "JOIN a.member m " +
                        "where a.project.id = :projectId", ProjectApplicantResponse.class)
                .setParameter("projectId", projectId)
                .getResultList();
    }

    public Optional<ProjectApplication> findByMemberIdAndProjectId(Long memberId, Long projectId) {
        return em.createQuery("select a from ProjectApplication a where a.member.id = :memberId and a.project.id = :projectId", ProjectApplication.class)
                .setParameter("memberId", memberId)
                .setParameter("projectId", projectId)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByMemberAndProjectId(Long memberId, Long projectId) {
        String jpql = "SELECT count(a) from ProjectApplication a " +
                "where a.member.id = :memberId and a.project.id = :projectId";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("memberId", memberId)
                .setParameter("projectId", projectId)
                .getSingleResult();

        return count > 0;
    }

    public void removeProjectApplication(ProjectApplication projectApplication) {
        em.remove(projectApplication);
    }
}
