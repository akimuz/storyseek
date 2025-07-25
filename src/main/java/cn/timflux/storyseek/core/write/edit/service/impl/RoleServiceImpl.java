package cn.timflux.storyseek.core.write.edit.service.impl;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.dto.RoleDTO;
import cn.timflux.storyseek.core.write.edit.entity.Role;
import cn.timflux.storyseek.core.write.edit.mapper.RoleMapper;
import cn.timflux.storyseek.core.write.edit.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: RoleServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午3:35
 * @Version 1.0
 */

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public Long addRole(RoleDTO dto) {
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        roleMapper.insert(role);
        log.info("新增角色：{}", role.getId());
        return role.getId();
    }

    @Override
    public void updateRole(RoleDTO dto) {
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        roleMapper.update(role);
        log.info("更新角色：{}", role.getId());
    }

    @Override
    public void deleteRole(Long id) {
        roleMapper.deleteById(id);
        log.info("删除角色：{}", id);
    }

    @Override
    public List<RoleDTO> getRolesByBook(Long bookId) {
        return roleMapper.findByBookId(bookId).stream().map(role -> {
            RoleDTO dto = new RoleDTO();
            BeanUtils.copyProperties(role, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public String getPromptText(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return "";
        List<Role> roles = roleMapper.selectBatchIds(ids);
        return roles.stream()
                .map(r -> String.format("角色名：%s\n简介：%s", r.getName(), r.getDescription()))
                .collect(Collectors.joining("\n\n"));
    }

    @Override
    public List<ListOptionDTO> getRoleOptions(Long bookId) {
        return roleMapper.findOptionsByBookId(bookId);
    }
}