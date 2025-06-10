package cn.timflux.storyseek.core.story.dto;

/**
 * ClassName: BeginningRequest
 * Package: cn.timflux.storyseek.core.story.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:47
 * @Version 1.0
 */
public class BeginningRequest {
    private String heroName;
    private String styleTag;
    private String worldSetting;
    private String otherReq;

    public String getHeroName() {
        return heroName;
    }
    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }
    public String getStyleTag() {
        return styleTag;
    }
    public void setStyleTag(String styleTag) {
        this.styleTag = styleTag;
    }
    public String getWorldSetting() {
        return worldSetting;
    }
    public void setWorldSetting(String worldSetting) {
        this.worldSetting = worldSetting;
    }
    public String getOtherReq() {
        return otherReq;
    }
    public void setOtherReq(String otherReq) {
        this.otherReq = otherReq;
    }
}
