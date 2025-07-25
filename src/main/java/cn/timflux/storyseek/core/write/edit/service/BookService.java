package cn.timflux.storyseek.core.write.edit.service;

import cn.timflux.storyseek.core.write.edit.dto.BookDTO;
import cn.timflux.storyseek.core.write.edit.entity.Book;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ClassName: BookService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午7:39
 * @Version 1.0
 */
public interface BookService extends IService<Book> {

    List<Book> listByUser(Long userId);

    BookDTO getBookById(Long bookId);

    boolean updateBook(Book book);

    boolean deleteBook(Long bookId);
}
