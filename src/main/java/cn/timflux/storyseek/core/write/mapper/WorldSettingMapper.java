package cn.timflux.storyseek.core.write.mapper;

import cn.timflux.storyseek.core.write.entity.WorldSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * ClassName: WorldSettingMapper
 * Package: cn.timflux.storyseek.core.write.mapper
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午3:59
 * @Version 1.0
 */
@Mapper
public interface WorldSettingMapper extends BaseMapper<WorldSetting> {

    @Select("SELECT * FROM world_setting WHERE book_id = #{bookId}")
    List<WorldSetting> findByBookId(Long bookId);

    @Insert("INSERT INTO world_setting (book_id, name, description) VALUES (#{bookId}, #{name}, #{description})")
    int insert(WorldSetting setting);

    @Update("UPDATE world_setting SET name = #{name}, description = #{description} WHERE id = #{id}")
    void update(WorldSetting setting);

    @Delete("DELETE FROM world_setting WHERE id = #{id}")
    void delete(Long id);
}
