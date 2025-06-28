package cn.timflux.storyseek.core.write.service;
import cn.timflux.storyseek.core.write.dto.RoleDTO;
import java.util.List;

/**
 * ClassName: RoleService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午3:34
 * @Version 1.0
 */

public interface RoleService {
    Long addRole(RoleDTO dto);
    void updateRole(RoleDTO dto);
    void deleteRole(Long id);
    List<RoleDTO> getRolesByBook(Long bookId);
    String getPromptText(List<Long> ids);
}