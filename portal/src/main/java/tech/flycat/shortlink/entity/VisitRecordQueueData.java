package tech.flycat.shortlink.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/6
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitRecordQueueData {
    private String code;
    private String remoteAddress;
    private Map<String, String> headers;
    private LocalDateTime createTime;
}
