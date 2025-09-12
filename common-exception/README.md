common-exception 模块实现规范
1. 模块定位

职责：
提供全局统一的异常定义、异常处理、错误码规范、统一返回结构。

原则：

不依赖任何业务模块，只能依赖基础框架（Spring Boot）。

必须被所有 service-* 模块依赖，保证全局一致性。

输出给外部的唯一内容：标准异常类、错误码枚举、统一返回体、全局异常处理器。

2. 目录结构建议
   common-exception
   └── src/main/java/com/easytiwu/common/exception
   ├── enums
   │    └── ErrorCode.java         # 错误码枚举
   ├── result
   │    └── Result.java            # 统一返回体
   ├── handler
   │    └── GlobalExceptionHandler.java # 全局异常处理器
   ├── BusinessException.java      # 业务异常
   ├── ParameterException.java     # 参数异常
   ├── SystemException.java        # 系统异常

3. 错误码设计规范

统一枚举类 ErrorCode：

public enum ErrorCode {
// 成功
SUCCESS(200, "操作成功"),

    // 客户端错误
    INVALID_PARAM(400, "参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),

    // 服务端错误
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 自定义业务错误
    BANK_NOT_FOUND(10001, "题库不存在"),
    QUESTION_NOT_FOUND(10002, "题目不存在");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}


规则：

系统级错误：100–999（HTTP 状态码兼容）。

业务级错误：10000+，按模块分段（如 Bank 模块 10001–10100）。

4. 异常类规范

基类：业务异常

public class BusinessException extends RuntimeException {
private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}


细分异常（可选）：

ParameterException：参数校验异常。

SystemException：不可预期的系统异常。

5. 统一返回体
   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   public class Result<T> {
   private int code;
   private String message;
   private T data;

   public static <T> Result<T> success(T data) {
   return new Result<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
   }

   public static Result<Void> success() {
   return new Result<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null);
   }

   public static Result<Void> error(ErrorCode errorCode) {
   return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
   }

   public static Result<Void> error(ErrorCode errorCode, String detail) {
   return new Result<>(errorCode.getCode(), detail, null);
   }
   }

6. 全局异常处理器

统一入口捕获

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.error(ErrorCode.INVALID_PARAM, msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(ErrorCode.INTERNAL_ERROR);
    }
}

7. 使用规范

抛出异常：

if (bank == null) {
throw new BusinessException(ErrorCode.BANK_NOT_FOUND);
}


返回成功：

return Result.success(bankList);

8. 注意事项

不要在 common-exception 模块里写业务逻辑。

错误码必须集中在 ErrorCode，禁止在业务代码里硬编码 new BusinessException(404, "xxx")。

所有接口返回必须用 Result<T>，保持前后端一致。