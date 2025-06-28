package cn.timflux.storyseek.core.write.service.impl;

import cn.timflux.storyseek.core.write.dto.BookDTO;
import cn.timflux.storyseek.core.write.entity.Book;
import cn.timflux.storyseek.core.write.mapper.BookMapper;
import cn.timflux.storyseek.core.write.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: BookServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description: 书籍服务实现类
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午7:40
 * @Version 1.0
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Autowired
    private BookMapper bookMapper;

    /**
     * 获取当前用户的所有书籍
     */
    @Override
    public List<Book> listByUser(Long userId) {
        return lambdaQuery().eq(Book::getUserId, userId).list();
    }

    /**
     * 获取指定书籍详情
     */
    @Override
    public BookDTO getBookById(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("书籍不存在");
        }

        BookDTO dto = new BookDTO();
        BeanUtils.copyProperties(book, dto); // 自动填充相同字段
        return dto;
    }

    /**
     * 更新书籍（可选，实际上 controller 已可直接调用 updateById）
     */
    @Override
    public boolean updateBook(Book book) {
        return updateById(book);
    }

    /**
     * 删除书籍（可选，实际上 controller 已可直接调用 removeById）
     */
    @Override
    public boolean deleteBook(Long bookId) {
        return removeById(bookId);
    }
}
