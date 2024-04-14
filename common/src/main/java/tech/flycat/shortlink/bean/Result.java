package tech.flycat.shortlink.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private ResultCodeEnum code;
    private String msg;
    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        return error(ResultCodeEnum.ERROR, msg);
    }

    public static <T> Result<T> error(ResultCodeEnum code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
