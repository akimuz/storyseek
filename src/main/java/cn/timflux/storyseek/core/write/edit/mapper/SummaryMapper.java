package cn.timflux.storyseek.core.write.edit.mapper;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.entity.Summary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * ClassName: SummaryMapper
 * Package: cn.timflux.storyseek.core.write.mapper
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午10:50
 * @Version 1.0
 */
@Mapper
public interface SummaryMapper extends BaseMapper<Summary> {

    @Insert("INSERT INTO summary (book_id, title, content, created_at) " +
            "VALUES (#{bookId}, #{title}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Summary summary);

    @Update("UPDATE summary SET title=#{title}, content=#{content} WHERE id=#{id}")
    int update(Summary summary);

    @Delete("DELETE FROM summary WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM summary WHERE book_id=#{bookId} ORDER BY created_at DESC")
    List<Summary> findByBookId(Long bookId);

    @Select("SELECT id, title AS label FROM summary WHERE book_id = #{bookId} ORDER BY created_at DESC")
    List<ListOptionDTO> findOptionsByBookId(@Param("bookId") Long bookId);

}

