package cn.timflux.storyseek.core.write.promptsea.mapper;

import cn.timflux.storyseek.core.write.promptsea.entity.PromptSnippetTagRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: PromptSnippetTagRelationMapper
 * Package: cn.timflux.storyseek.core.write.promptsea.mapper
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 上午2:10
 * @Version 1.0
 */
@Mapper
public interface PromptSnippetTagRelationMapper extends BaseMapper<PromptSnippetTagRelation> {

    @Select("SELECT snippet_id FROM prompt_snippet_tag_relation WHERE tag_id = #{tagId}")
    List<Long> findSnippetIdsByTagId(@Param("tagId") Long tagId);

    @Select("SELECT tag_id FROM prompt_snippet_tag_relation WHERE snippet_id = #{snippetId}")
    List<Long> findTagIdsBySnippetId(@Param("snippetId") Long snippetId);

    List<Long> findSnippetIdsByTagIds(@Param("tagIds") List<Long> tagIds);

}
