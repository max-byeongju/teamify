package matching.teamify.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import matching.teamify.domain.FavoriteProject;
import matching.teamify.domain.FavoriteStudy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FavoriteRepository {

    private final EntityManager em;

    public void saveFavoriteProject(FavoriteProject favoriteProject) {
        em.persist(favoriteProject);
    }

    public Optional<FavoriteProject> findByMemberIdAndProjectId(Long memberId, Long projectId) {
        return em.createQuery("select f from FavoriteProject f where f.member.id = :memberId and f.project.id = :projectId", FavoriteProject.class)
                .setParameter("memberId", memberId)
                .setParameter("projectId", projectId)
                .getResultStream()
                .findFirst();
    }

    public List<Long> findFavoriteProjectIds(Long memberId) {
        return em.createQuery("select f.project.id from FavoriteProject f where f.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<FavoriteProject> findFavoriteProjects(Long memberId) {
        return em.createQuery("select f from FavoriteProject f join fetch f.project p join fetch p.member where f.member.id = :memberId", FavoriteProject.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public void deleteFavoriteProject(FavoriteProject favoriteProject) {
        em.remove(favoriteProject);
    }

    public void saveFavoriteStudy(FavoriteStudy favoriteStudy) {
        em.persist(favoriteStudy);
    }

    public Optional<FavoriteStudy> findByMemberIdAndStudyId(Long memberId, Long studyId) {
        return em.createQuery("select f from FavoriteStudy f where f.member.id = :memberId and f.study.id = :studyId", FavoriteStudy.class)
                .setParameter("memberId", memberId)
                .setParameter("studyId", studyId)
                .getResultStream()
                .findFirst();
    }

    public List<Long> findFavoriteStudyIds(Long memberId) {
        return em.createQuery("select f.study.id from FavoriteStudy f where f.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<FavoriteStudy> findFavoriteStudies(Long memberId) {
        return em.createQuery("select f from FavoriteStudy f join fetch f.study s join fetch s.member where f.member.id = :memberId", FavoriteStudy.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public void deleteFavoriteStudy(FavoriteStudy favoriteStudy) {
        em.remove(favoriteStudy);
    }
}
