package tech.flycat.shortlink.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import tech.flycat.shortlink.entity.VisitRecord;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/5
 */
@Repository
public interface VisitRecordRepository extends R2dbcRepository<VisitRecord, Long> {

}
