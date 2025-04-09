package matching.teamify.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Comment;
import matching.teamify.dto.comment.CommentResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;

    public void save(Comment comment) {
        em.persist(comment);
    }

    public Comment findById(Long commentId) {
        return em.find(Comment.class, commentId);
    }

    public List<CommentResponse> findByProjectId(Long projectId) {
        return em.createQuery("select new matching.teamify.dto.comment.CommentResponse(" +
                        "c.member.id ,c.member.nickName,c.member.picture, c.localDateTime, c.comment) " +
                        "from Comment c " +
                        "where c.project.id = :id", CommentResponse.class)
                .setParameter("id", projectId)
                .getResultList();
    }

    public List<CommentResponse> findByStudyId(Long studyId) {
        return em.createQuery("select new matching.teamify.dto.comment.CommentResponse(" +
                        "c.member.id, c.member.nickName, c.member.picture, c.localDateTime, c.comment) " +
                        "from Comment c " +
                        "where c.study.id = :id", CommentResponse.class)
                .setParameter("id", studyId)
                .getResultList();
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }
}
