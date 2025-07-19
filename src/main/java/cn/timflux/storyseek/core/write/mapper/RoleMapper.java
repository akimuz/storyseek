package cn.timflux.storyseek.core.write.mapper;

import cn.timflux.storyseek.core.write.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * ClassName: RoleMapper
 * Package: cn.timflux.storyseek.core.write.mapper
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午3:28
 * @Version 1.0
 */

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Insert("INSERT INTO role (book_id, name, description, created_at) VALUES (#{bookId}, #{name}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);

    @Update("UPDATE role SET name=#{name}, description=#{description} WHERE id=#{id}")
    int update(Role role);

    @Delete("DELETE FROM role WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM role WHERE book_id=#{bookId} ORDER BY created_at DESC")
    List<Role> findByBookId(Long bookId);

    /**
     * 轻量级 option 查询：id, name → ListOptionDTO.id, ListOptionDTO.label
     */
    @Select("SELECT id, name AS label FROM role WHERE book_id = #{bookId} ORDER BY created_at DESC")
    List<ListOptionDTO> findOptionsByBookId(@Param("bookId") Long bookId);

}