package cn.timflux.storyseek.core.story.dto;
import java.util.List;

/**
 * ClassName: StoryResponseDTO
 * Package: cn.timflux.storyseek.core.story.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:59
 * @Version 1.0
 */
public class StoryResponseDTO {
    /**
     * 分片流式输出后，前端会收完 flux 之后，
     * 通过 SSE 接口监听到一个“终结事件”，
     * 其 data 就是这个对象的 JSON 序列化。
     */
    private List<OptionDTO> options;
    private boolean ending;    // true 表示这是最后一段结尾，不再显示选项

    // getters & setters
    public List<OptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDTO> options) {
        this.options = options;
    }

    public boolean isEnding() {
        return ending;
    }

    public void setEnding(boolean ending) {
        this.ending = ending;
    }
}
