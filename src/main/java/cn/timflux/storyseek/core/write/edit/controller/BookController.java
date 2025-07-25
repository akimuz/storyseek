package cn.timflux.storyseek.core.write.edit.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.edit.dto.BookCreateDTO;
import cn.timflux.storyseek.core.write.edit.dto.BookDTO;
import cn.timflux.storyseek.core.write.edit.entity.Book;
import cn.timflux.storyseek.core.write.edit.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: BookController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午7:41
 * @Version 1.0
 */

@RestController
@RequestMapping("/api/write/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ApiResponse<List<Book>> listMyBooks() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Book> books = bookService.listByUser(userId);
    return ApiResponse.ok(books);
}

    @PostMapping
    public ApiResponse<Book> create(@RequestBody BookCreateDTO dto) {
        StpUtil.checkLogin();

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setType(dto.getType());
        book.setDescription(dto.getDesc());
        book.setUserId(StpUtil.getLoginIdAsLong());

        bookService.save(book);
        return ApiResponse.ok(book);
    }

    @GetMapping("/{bookId}")
    public ApiResponse<BookDTO> getBook(@PathVariable Long bookId) {
        return ApiResponse.ok(bookService.getBookById(bookId));
    }

    @PutMapping("/{bookId}")
    public ApiResponse<Book> update(@PathVariable Long bookId, @RequestBody BookCreateDTO dto) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();

        Book book = bookService.getById(bookId);
        if (book == null || !book.getUserId().equals(userId)) {
            return ApiResponse.error("书籍不存在或无权限");
        }

        book.setTitle(dto.getTitle());
        book.setType(dto.getType());
        book.setDescription(dto.getDesc());

        bookService.updateById(book);
        return ApiResponse.ok(book);
    }

    @DeleteMapping("/{bookId}")
    public ApiResponse<?> delete(@PathVariable Long bookId) {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();

        Book book = bookService.getById(bookId);
        if (book == null || !book.getUserId().equals(userId)) {
            return ApiResponse.error("书籍不存在或无权限");
        }

        bookService.removeById(bookId);
        return ApiResponse.ok("删除成功");
    }

}