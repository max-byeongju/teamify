package matching.teamify.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import matching.teamify.domain.Comment;
import matching.teamify.dto.comment.CommentResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;

    public void save(Comment comment) {
        em.persist(comment);
    }

    public Optional<Comment> findById(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        return Optional.ofNullable(comment);
    }

    public List<CommentResponse> findByProjectId(Long projectId) {
        return em.createQuery("select new matching.teamify.dto.comment.CommentResponse(" +
                        "c.member.id ,c.member.nickName,c.member.picture, c.createdDate, c.comment) " +
                        "from Comment c " +
                        "where c.project.id = :id", CommentResponse.class)
                .setParameter("id", projectId)
                .getResultList();
    }

    public List<CommentResponse> findByStudyId(Long studyId) {
        return em.createQuery("select new matching.teamify.dto.comment.CommentResponse(" +
                        "c.member.id, c.member.nickName, c.member.picture, c.createdDate, c.comment) " +
                        "from Comment c " +
                        "where c.study.id = :id", CommentResponse.class)
                .setParameter("id", studyId)
                .getResultList();
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }
}
