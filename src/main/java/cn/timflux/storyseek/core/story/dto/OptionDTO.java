package cn.timflux.storyseek.core.story.dto;

/**
 * ClassName: OptionDTO
 * Package: cn.timflux.storyseek.core.story.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:58
 * @Version 1.0
 */
public class OptionDTO {
    private String id;
    private String title;

    public OptionDTO() {}

    public OptionDTO(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}



