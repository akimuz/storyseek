package cn.timflux.storyseek.core.write.dto;

/**
 * ClassName: BookCreateDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午7:38
 * @Version 1.0
 */
public class BookCreateDTO {
    private String title;
    private String type;
    private String desc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
