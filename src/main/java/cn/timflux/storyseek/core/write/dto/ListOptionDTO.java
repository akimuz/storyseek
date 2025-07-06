package cn.timflux.storyseek.core.write.dto;
import lombok.Data;
/**
 * ClassName: ListOptionDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 * 通用下拉选项 DTO，只包含 id 和 label
 * @Author 一剑霜寒十四州
 * @Create 2025/6/30 上午2:02
 * @Version 1.0
 */
@Data
public class ListOptionDTO {
    private Long id;
    private String label;
    private String username;
}
