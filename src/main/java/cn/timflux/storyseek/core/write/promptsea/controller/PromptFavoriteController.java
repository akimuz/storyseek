package cn.timflux.storyseek.core.write.promptsea.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptSnippetDTO;
import cn.timflux.storyseek.core.write.edit.entity.PromptSnippet;
import cn.timflux.storyseek.core.write.promptsea.service.PromptSnippetFavoriteService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: PromptSnippetFavoriteController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午2:03
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/prompt/favorite")
public class PromptFavoriteController {

    private final PromptSnippetFavoriteService favoriteService;

    public PromptFavoriteController(PromptSnippetFavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 收藏或取消收藏
     * 需要登录
     */
    @SaCheckLogin
    @PostMapping("/toggle")
    public ApiResponse<Void> toggleFavorite(@RequestParam Long snippetId,
                                            @RequestParam Boolean favorite) {
        Long userId = StpUtil.getLoginIdAsLong();
        favoriteService.setFavorite(userId, snippetId, favorite);
        return ApiResponse.ok();
    }

    /**
     * 获取用户收藏列表
     * 需要登录
     */
    @SaCheckLogin
    @GetMapping("/list")
    public ApiResponse<List<PromptSnippetDTO>> getUserFavorites(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<PromptSnippet> favorites = favoriteService.getUserFavorites(userId, page, size);
        List<PromptSnippetDTO> dtos = favorites.stream().map(snippet -> {
            PromptSnippetDTO dto = new PromptSnippetDTO();
            BeanUtils.copyProperties(snippet, dto);
            return dto;
        }).collect(Collectors.toList());

        return ApiResponse.ok(dtos);
    }

    /**
     * 判断是否收藏某提示词
     * 需要登录
     */
    @SaCheckLogin
    @GetMapping("/is-favorite")
    public ApiResponse<Boolean> isFavorite(@RequestParam Long snippetId) {
        Long userId = StpUtil.getLoginIdAsLong();
        boolean result = favoriteService.isFavorite(userId, snippetId);
        return ApiResponse.ok(result);
    }

    @GetMapping("/listByUser")
    public ApiResponse<List<ListOptionDTO>> listByUser(@RequestParam Long userId){
        if (userId == null) {
            return ApiResponse.error("Missing or invalid userId");
            }
        return ApiResponse.ok(favoriteService.getFavorPromptOptions(userId));
    }

    @GetMapping("/optionlist")
    public ApiResponse<List<ListOptionDTO>> optionList(@RequestParam Long userId) {
        if (userId == null) {
            return ApiResponse.error("Missing or invalid userId");
            }
        return ApiResponse.ok(favoriteService.getFavorPromptOptions(userId));
    }
}
