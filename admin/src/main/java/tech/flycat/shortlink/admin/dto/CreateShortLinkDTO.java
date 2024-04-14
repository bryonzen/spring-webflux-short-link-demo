package tech.flycat.shortlink.admin.dto;

import lombok.Data;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/6
 */
@Data
public class CreateShortLinkDTO {
    private String name;
    private String url;
}
