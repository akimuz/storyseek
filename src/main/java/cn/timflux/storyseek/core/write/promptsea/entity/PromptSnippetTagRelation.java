package cn.timflux.storyseek.core.write.promptsea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * ClassName: PromptSnippetTagRelation
 * Package: cn.timflux.storyseek.core.write.promptsea.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 上午2:09
 * @Version 1.0
 */
@Data
@TableName("prompt_snippet_tag_relation")
public class PromptSnippetTagRelation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long snippetId;
    private Long tagId;
}

