package tech.flycat.shortlink.entity;

import java.time.LocalDateTime;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "visit_record")
public class VisitRecord {
    @Id
    private Long id;

    /**
     * 短链code
     */
    private String code;

    /**
     * 访问主机名
     */
    private String host;

    /**
     * 访问代理
     */
    private String userAgent;

    /**
     * ip地址
     */
    private String ipAddr;

    /**
     * 访问来源
     */
    private String referer;

    /**
     * 操作系统
     */
    private String operateOs;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * ip解析国家
     */
    private String ipCountry;

    /**
     * ip解析省份
     */
    private String ipProvince;

    /**
     * ip解析城市
     */
    private String ipCity;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
