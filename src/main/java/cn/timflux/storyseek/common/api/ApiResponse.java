package cn.timflux.storyseek.common.api;

import lombok.Data;

/**
 * ClassName: ApiResponse
 * Package: cn.timflux.storyseek.common.exception.api
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 上午11:45
 * @Version 1.0
 */
@Data
public class ApiResponse<T> {
    private int code;
    private String msg;
    private T data;

    public ApiResponse() {}

    public ApiResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(200, "ok", null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "ok", data);
    }

    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(500, msg, null);
    }
}

