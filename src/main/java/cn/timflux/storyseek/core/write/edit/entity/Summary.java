package cn.timflux.storyseek.core.write.edit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * ClassName: Summary
 * Package: cn.timflux.storyseek.core.write.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午10:15
 * @Version 1.0
 */
@Data
@TableName("summary")
public class Summary {
    private Long id;
    private Long bookId;
    private String title;
    private String content;
}

