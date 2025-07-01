package cn.timflux.storyseek.core.write.dto;

import lombok.Data;

/**
 * ClassName: SummaryDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午10:11
 * @Version 1.0
 */
@Data
public class SummaryDTO {
    private Long id;
    private Long bookId;
    private String title;
    private String content;
}
