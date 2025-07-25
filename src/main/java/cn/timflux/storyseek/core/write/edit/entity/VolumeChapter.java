package cn.timflux.storyseek.core.write.edit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * ClassName: VolumeChapter
 * Package: cn.timflux.storyseek.core.write.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:05
 * @Version 1.0
 */
@Data
@TableName("book_volume_chapter")
public class VolumeChapter {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;    // 所属书籍ID
    private Long parentId;  // 父节点ID，顶层卷的parentId为0或null
    private String name;    // 卷或章节名称
    private Integer type;   // 类型，1=卷，2=章
    private Integer orderNum; // 排序字段，方便前端排序展示
    private String content; // 正文
    private String draftSummary; // 简易梗概（用户输入）
    private String summary;      // 智能梗概（大模型生成）
}

