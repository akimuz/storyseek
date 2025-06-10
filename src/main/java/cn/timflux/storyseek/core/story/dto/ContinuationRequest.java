package cn.timflux.storyseek.core.story.dto;

/**
 * ClassName: ContinuationRequest
 * Package: cn.timflux.storyseek.core.story.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:48
 * @Version 1.0
 */
public class ContinuationRequest {
    private String currentStory;
    private String choiceId;

    public String getCurrentStory() {
        return currentStory;
    }
    public void setCurrentStory(String currentStory) {
        this.currentStory = currentStory;
    }
    public String getChoiceId() {
        return choiceId;
    }
    public void setChoiceId(String choiceId) {
        this.choiceId = choiceId;
    }
}
