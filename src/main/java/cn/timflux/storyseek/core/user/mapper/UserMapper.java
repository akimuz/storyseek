package cn.timflux.storyseek.core.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.timflux.storyseek.core.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserMapper
 * Package: cn.timflux.storyseek.core.user.entity.mapper
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午6:55
 * @Version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}