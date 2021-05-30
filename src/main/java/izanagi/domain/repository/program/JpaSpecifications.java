package izanagi.domain.repository.program;

import izanagi.domain.entity.ChannelEntity;
import izanagi.domain.entity.ProgramEntity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class JpaSpecifications {

  public static Specification<ProgramEntity> isLive(long now) {
    Specification<ProgramEntity> programEntitySpecification = new Specification<ProgramEntity>() {
      @Override
      public Predicate toPredicate(
          Root<ProgramEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder
      ) {
        return criteriaBuilder.and(
            criteriaBuilder.lessThanOrEqualTo(root.get("begin"), now),
            criteriaBuilder.greaterThanOrEqualTo(root.get("end"), now)
        );
      }
    };
    return programEntitySpecification;
  }

  public static Specification<ProgramEntity> equalsChannelRemoteControl(int channelRemoteControl) {
    Specification<ProgramEntity> programEntitySpecification = new Specification<ProgramEntity>() {
      @Override
      public Predicate toPredicate(
          Root<ProgramEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder
      ) {
        final Join<ProgramEntity, ChannelEntity> programEntityChannelEntityJoin =
            root.join("channelEntity", JoinType.LEFT);
        return criteriaBuilder.equal(programEntityChannelEntityJoin
            .get("channelRemoteControl"), channelRemoteControl);
      }
    };
    return programEntitySpecification;
  }

  public static Specification<ChannelEntity> equalsChannel(String id) {
    Specification<ChannelEntity> programEntitySpecification = new Specification<ChannelEntity>() {
      @Override
      public Predicate toPredicate(
          Root<ChannelEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder
      ) {
        return criteriaBuilder.equal(root.get("id"), id);
      }
    };
    return programEntitySpecification;
  }
}
