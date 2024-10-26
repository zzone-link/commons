package link.zzone.commons.http.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chrischen
 * @date 2024/10/8
 * @description 返回结果对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result<T> {

    private static final String DEFAULT_ERROR_CODE = "20000";

    /**
     * 返回结果状态码
     * 0为成功，9为失败
     */
    private int status;

    /**
     * 返回结果信息
     * status为0时，默认为success；
     * status为9时，为错误描述信息，比如服务暂停
     */
    private String message;

    /**
     * 返回结果数据对象
     * 数据对象或数据对象集合
     */
    private T data;

    /**
     * 错误代码 共5位
     * 第一位：错误级别，1：系统级错误；2：服务级错误
     * 第二、三位：模块代码，01：认证模块；02：商家模块；03：用户模块；04：支付模块；05：平台模块
     * 第四、五位：具体功能代码，各模块自己定义
     * status为0时，默认为null，不返回
     * status为9时，默认为20000
     */
    private String errorCode;

    /**
     * 错误详情（不展示，仅供快速定位问题使用）
     */
    private String errorDetail;

    /**
     * Result构造函数
     *
     * @param status  返回结果状态码
     * @param message 用于展示的错误提示
     */
    public Result(int status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    /**
     * 返回成功信息
     *
     * @param data 返回结果数据对象
     * @return Result
     */
    public static <T> Result<T> getSuccessResult(T data) {
        return Result.<T>builder()
                .status(0)
                .message("success")
                .data(data)
                .build();
    }

    /**
     * 返回失败信息
     *
     * @param message 错误描述信息
     * @return Result
     */
    public static <T> Result<T> getErrorResult(String message) {
        return getErrorResult(DEFAULT_ERROR_CODE, message, "");
    }

    /**
     * 返回失败信息
     *
     * @param message     用于展示的错误提示
     * @param errorDetail 错误详情
     * @return Result
     */
    public static <T> Result<T> getErrorResult(String message, String errorDetail) {
        return getErrorResult(DEFAULT_ERROR_CODE, message, errorDetail);
    }

    /**
     * 返回失败信息
     *
     * @param errorCode   错误代码
     * @param message     用于展示的错误提示
     * @param errorDetail 错误详情
     * @return Result
     */
    public static <T> Result<T> getErrorResult(String errorCode, String message, String errorDetail) {
        return Result.<T>builder()
                .status(9)
                .errorCode(errorCode)
                .message(message)
                .errorDetail(errorDetail)
                .build();
    }

    /**
     * 是否成功
     * @return 是否成功
     */
    public boolean isSuccess() {
        return 0 == this.status;
    }
}
