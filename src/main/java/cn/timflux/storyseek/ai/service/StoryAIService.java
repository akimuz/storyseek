package cn.timflux.storyseek.ai.service;

import cn.timflux.storyseek.ai.model.StoryBeginningStrategy;
import cn.timflux.storyseek.ai.model.StoryContinuationStrategy;
import cn.timflux.storyseek.ai.model.StoryEndingStrategy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
/**
 * ClassName: StoryAIService
 * Package: cn.timflux.storyseek.ai.model.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:50
 * @Version 1.0
 */
@Service
public class StoryAIService {

    private final StoryBeginningStrategy beginningStrategy;
    private final StoryContinuationStrategy continuationStrategy;
    private final StoryEndingStrategy endingStrategy;

    public StoryAIService(
        StoryBeginningStrategy beginningStrategy,
        StoryContinuationStrategy continuationStrategy,
        StoryEndingStrategy endingStrategy
    ) {
        this.beginningStrategy    = beginningStrategy;
        this.continuationStrategy = continuationStrategy;
        this.endingStrategy       = endingStrategy;
    }

    public Flux<String> generateBeginning(Map<String, Object> formContext) {
        return beginningStrategy.generateBeginning(formContext);
    }

    public Flux<String> generateContinuation(Map<String, Object> context) {
        return continuationStrategy.generateContinuation(context);
    }

    public Flux<String> generateEnding(Map<String, Object> storyContext) {
        return endingStrategy.generateEnding(storyContext);
    }
}
