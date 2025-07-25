package cn.timflux.storyseek.core.write.edit.dto;

import lombok.Data;

/**
 * ClassName: VolumeChapterCreateDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:10
 * @Version 1.0
 */
@Data
public class VolumeChapterCreateDTO {
    private Long bookId;
    private Long parentId;  // 卷的parentId可不传或者传0，章必须传卷的id
    private String name;
}