package cn.timflux.storyseek.core.write.dto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: VolumeChapterTreeDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:12
 * @Version 1.0
 */
@Data
public class VolumeChapterTreeDTO {
    private Long id;
    private Long bookId;
    private Long parentId;
    private String name;
    private Integer type; // 1=卷 2=章
    private Integer orderNum;

    private List<VolumeChapterTreeDTO> children = new ArrayList<>();
}