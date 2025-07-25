package cn.timflux.storyseek.core.write.edit.mapper;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.entity.PromptSnippetFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: PromptSnippetFavoriteMapper
 * Package: cn.timflux.storyseek.core.write.mapper
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午2:10
 * @Version 1.0
 */
@Mapper
public interface PromptSnippetFavoriteMapper extends BaseMapper<PromptSnippetFavorite> {

    @Select("SELECT snippet_id FROM prompt_snippet_favorite WHERE user_id = #{userId}")
    List<Long> selectSnippetIdsByUserId(Long userId);

    @Select("""
        SELECT 
            psf.snippet_id AS id,
            psf.title AS label,
            psf.author_name AS username
        FROM 
            prompt_snippet_favorite psf
        WHERE 
            psf.user_id = #{userId}
        ORDER BY 
            psf.created_at DESC
    """)
    List<ListOptionDTO> findOptionsByUserId(@Param("userId") Long userId);

}
