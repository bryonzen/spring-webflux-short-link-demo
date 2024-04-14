package tech.flycat.shortlink.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "redirect_link_info")
public class RedirectLinkInfo {
    @Id
    private Long id;

    /**
     * 短链code
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 原url
     */
    private String url;

    /**
     * 原url md5值
     */
    private String urlMd5;

    /**
     * 是否有效
     */
    private Boolean valid;

    /**
     * 访问次数
     */
    private Integer visitTimes;

    /**
     * 操作人
     */
    private Long operatorId;

    /**
     *
     */
    private LocalDateTime createTime;

    /**
     *
     */
    private LocalDateTime updateTime;
}
