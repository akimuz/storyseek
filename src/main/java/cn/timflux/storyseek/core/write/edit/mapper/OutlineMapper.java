package cn.timflux.storyseek.core.write.edit.mapper;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.entity.Outline;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: OutlineMapper
 * Package: cn.timflux.storyseek.core.write.mapper
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午3:43
 * @Version 1.0
 */
@Mapper
public interface OutlineMapper extends BaseMapper<Outline> {
    @Select("SELECT id, title AS label FROM outline WHERE book_id = #{bookId} ORDER BY created_at DESC")
    List<ListOptionDTO> findOptionsByBookId(@Param("bookId") Long bookId);
}
