package cn.timflux.storyseek.core.write.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.timflux.storyseek.core.user.entity.User;
import cn.timflux.storyseek.core.user.service.UserService;
import cn.timflux.storyseek.core.write.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.dto.PromptSnippetDTO;
import cn.timflux.storyseek.core.write.entity.PromptSnippet;
import cn.timflux.storyseek.core.write.mapper.PromptSnippetMapper;
import cn.timflux.storyseek.core.write.service.PromptSnippetService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClassName: PromptSnippetServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:26
 * @Version 1.0
 */
@Slf4j
@Service
public class PromptSnippetServiceImpl implements PromptSnippetService {

    private final PromptSnippetMapper mapper;
    private final UserService userService;

    public PromptSnippetServiceImpl(PromptSnippetMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @Override
    public Long addPromptSnippet(PromptSnippetDTO dto) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        PromptSnippet snippet = new PromptSnippet();
        snippet.setUserId(currentUserId);
        snippet.setTitle(dto.getTitle());
        snippet.setSummary(dto.getSummary());
        snippet.setContent(dto.getContent());
        snippet.setPublished(dto.getPublished() != null ? dto.getPublished() : false);
        mapper.insert(snippet);
        return snippet.getId();
    }

    @Override
    public void updatePromptSnippet(PromptSnippetDTO dto) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        PromptSnippet existing = mapper.selectById(dto.getId());

        if (existing == null || !existing.getUserId().equals(currentUserId)) {
            throw new RuntimeException("无权限修改该提示词");
        }

        existing.setTitle(dto.getTitle());
        existing.setSummary(dto.getSummary());
        existing.setContent(dto.getContent());
        existing.setPublished(dto.getPublished());

        mapper.updateById(existing);
    }

    @Override
    public void deletePromptSnippet(Long id) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        PromptSnippet snippet = mapper.selectById(id);

        if (snippet == null || !snippet.getUserId().equals(currentUserId)) {
            throw new RuntimeException("无权限删除该提示词");
        }

        mapper.deleteById(id);
    }

    @Override
    public String getPromptText(List<Long> ids) {
        log.debug("传入的ID：" + ids);
        if (ids == null || ids.isEmpty()) return "";

        Long userId = StpUtil.getLoginIdAsLong();

        // 拼接成 "1,2,3" 的形式传给 SQL
        String joinedIds = ids.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));

        List<String> contents = mapper.getCollectedPromptContents(userId, joinedIds);
        log.debug("拼接ID：" + joinedIds);

        return contents.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n\n"));
    }



    @Override
    public List<PromptSnippetDTO> getSnippetListSafe(String keyword, String orderBy, int limit, int offset, Long currentUserId) {
        QueryWrapper<PromptSnippet> wrapper = new QueryWrapper<>();
        wrapper.eq("published", true);

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("title", keyword);
        }

        Set<String> allowedOrderFields = Set.of("created_at", "updated_at", "title");
        if (allowedOrderFields.contains(orderBy)) {
            wrapper.orderByDesc(orderBy);
        } else {
            wrapper.orderByDesc("created_at");
        }

        int pageNum = offset / limit + 1;
        Page<PromptSnippet> page = new Page<>(pageNum, limit);
        List<PromptSnippet> list = mapper.selectPage(page, wrapper).getRecords();

        return list.stream()
                .map(snippet -> toDTO(snippet, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public PromptSnippetDTO getSnippetByIdSafe(Long id, Long currentUserId) {
        PromptSnippet snippet = mapper.selectById(id);
        if (snippet == null || !snippet.getPublished()) {
            return null;
        }
        return toDTO(snippet, currentUserId);
    }

    @Override
    public List<PromptSnippetDTO> getSnippetByUser(Long userId) {
        return mapper.findByUserId(userId).stream().map(snippet -> {
            PromptSnippetDTO dto = new PromptSnippetDTO();
            BeanUtils.copyProperties(snippet, dto);

            User user = userService.getById(snippet.getUserId());
            dto.setAuthorName(user != null ? user.getUsername() : "未知用户");

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ListOptionDTO> getPromptOptions(Long userId) {
        return mapper.findOptionsByUserId(userId);
    }

    private PromptSnippetDTO toDTO(PromptSnippet snippet, Long currentUserId) {
        PromptSnippetDTO dto = new PromptSnippetDTO();
        dto.setId(snippet.getId());
        dto.setUserId(snippet.getUserId());
        dto.setTitle(snippet.getTitle());
        dto.setSummary(snippet.getSummary());
        dto.setPublished(snippet.getPublished());

        if (currentUserId != null && currentUserId.equals(snippet.getUserId())) {
            dto.setContent(snippet.getContent());
        } else {
            dto.setContent(null); // 非作者不返回内容
        }

        User user = userService.getById(snippet.getUserId());
        dto.setAuthorName(user != null ? user.getUsername() : "未知用户");

        return dto;
    }

    @Override
    public List<PromptSnippet> getDefaultSystemPromptSnippets() {
        // 获取默认提示词
        QueryWrapper<PromptSnippet> wrapper = new QueryWrapper<>();
        wrapper.eq("published", true);
        wrapper.eq("is_default", true);
        return mapper.selectList(wrapper);
    }
}
