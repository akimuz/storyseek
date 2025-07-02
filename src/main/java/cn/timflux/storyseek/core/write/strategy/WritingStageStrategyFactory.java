package cn.timflux.storyseek.core.write.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName: WritingStageStrategyFactory
 * Package: cn.timflux.storyseek.core.write.strategy
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:22
 * @Version 1.0
 */
@Slf4j
@Component
public class WritingStageStrategyFactory {

    @Autowired
    private OutlineStrategy outline;
    @Autowired
    private DetailedOutlineStrategy detailed;
    @Autowired
    private ContentStrategy content;
    @Autowired
    private RoleStrategy role;
    @Autowired
    private WorldStrategy world;
    @Autowired
    private SummaryStrategy summary;

    public WritingStageStrategy getStrategy(String stage) {
        log.info("获取写作阶段策略: {}", stage);

        return switch (stage.toLowerCase()) {
            case "role" -> role;
            case "world" -> world;
            case "outline" -> outline;
            case "suboutline" -> detailed;
            case "text" -> content;
            case "summary" -> summary;
            default -> {
                log.warn("未知的写作类型请求: {}", stage);
                throw new IllegalArgumentException("Unsupported stage: " + stage);
            }
        };
    }
}
