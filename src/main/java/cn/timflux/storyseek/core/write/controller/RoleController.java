package cn.timflux.storyseek.core.write.controller;
import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.dto.RoleDTO;
import cn.timflux.storyseek.core.write.service.RoleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ClassName: RoleController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午3:35
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ApiResponse<Long> create(@RequestBody RoleDTO dto) {
        return ApiResponse.ok(roleService.addRole(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        dto.setId(id);
        roleService.updateRole(dto);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.ok();
    }

    @GetMapping
    public ApiResponse<List<RoleDTO>> list(@RequestParam Long bookId) {
        return ApiResponse.ok(roleService.getRolesByBook(bookId));
    }

    // 下拉选项接口
    @GetMapping("/optionlist")
    public ApiResponse<List<ListOptionDTO>> optionList(@RequestParam Long bookId) {
        List<ListOptionDTO> list = roleService.getRoleOptions(bookId);
        return ApiResponse.ok(list);
    }
}