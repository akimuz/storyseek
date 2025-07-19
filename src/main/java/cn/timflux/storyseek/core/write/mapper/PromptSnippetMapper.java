package cn.timflux.storyseek.core.write.mapper;

import cn.timflux.storyseek.core.write.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.entity.PromptSnippet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * ClassName: PromptSnippetMapper
 * Package: cn.timflux.storyseek.core.write.mapper
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:53
 * @Version 1.0
 */
@Mapper
public interface PromptSnippetMapper extends BaseMapper<PromptSnippet> {
    @Update("UPDATE prompt_snippet SET favorite_count = favorite_count + #{delta} WHERE id = #{id}")
    int updateFavoriteCount(@Param("id") Long id, @Param("delta") int delta);

    @Select("SELECT * FROM prompt_snippet WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<PromptSnippet> findByUserId(Long userId);

    @Select("""
    SELECT 
        ps.id,
        ps.title AS label,
        u.username
    FROM 
        prompt_snippet ps
    JOIN 
        t_user u ON ps.user_id = u.id
    WHERE 
        ps.user_id = #{userId}
    ORDER BY 
        ps.created_at DESC
""")
    List<ListOptionDTO> findOptionsByUserId(@Param("userId") Long userId);

    @Select("""
        SELECT s.content
        FROM prompt_snippet s
        JOIN prompt_snippet_favorite f ON s.id = f.snippet_id
        WHERE f.user_id = #{userId}
          AND f.snippet_id IN (${ids})
    """)
    List<String> getCollectedPromptContents(@Param("userId") Long userId, @Param("ids") String ids);

}
