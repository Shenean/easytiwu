# Common Exception 模块使用指南

## 概述

`common-exception` 是 EasyTiwu 项目的通用异常处理模块，提供了统一的异常处理机制、标准化的错误响应格式和完善的异常分类体系。

## 核心特性

- **统一异常处理**: 通过 `@RestControllerAdvice` 提供全局异常处理
- **标准化响应**: 统一的 `Result<T>` 响应格式
- **分层异常设计**: 业务异常、系统异常、参数异常的清晰分类
- **丰富的错误码**: 预定义的错误码枚举，支持国际化
- **开发友好**: 详细的异常日志和调试信息

## 快速开始

### 1. 添加依赖

在你的服务模块 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.easytiwu</groupId>
    <artifactId>common-exception</artifactId>
</dependency>
```

### 2. 启用异常处理

在你的 Spring Boot 应用中，异常处理器会自动生效。确保你的包扫描包含了 `com.easytiwu.commonexception`：

```java
@SpringBootApplication(scanBasePackages = {
    "com.easytiwu.yourservice",
    "com.easytiwu.commonexception"
})
public class YourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourServiceApplication.class, args);
    }
}
```

## 异常类型

### 1. BusinessException (业务异常)

用于处理业务逻辑中的异常情况：

```java
// 使用预定义错误码
throw new BusinessException(ErrorCode.USER_NOT_FOUND);

// 使用自定义消息
throw new BusinessException("用户余额不足");

// 使用错误码和自定义消息
throw new BusinessException(ErrorCode.BUSINESS_ERROR, "订单状态异常");
```

### 2. ParameterException (参数异常)

用于处理参数校验失败的情况：

```java
// 参数为空
throw new ParameterException(ErrorCode.PARAM_MISSING, "用户ID不能为空");

// 参数格式错误
throw new ParameterException(ErrorCode.PARAM_FORMAT_ERROR, "邮箱格式不正确");
```

### 3. SystemException (系统异常)

用于处理系统级别的异常：

```java
// 数据库连接异常
throw new SystemException(ErrorCode.DATABASE_ERROR, "数据库连接失败");

// 外部服务调用异常
throw new SystemException(ErrorCode.EXTERNAL_SERVICE_ERROR, "支付服务不可用");
```

## 统一响应格式

所有 API 响应都使用统一的 `Result<T>` 格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据
  },
  "success": true,
  "timestamp": "2024-01-01T12:00:00",
  "traceId": "abc123"
}
```

### 成功响应

```java
@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return Result.success(user);
    }
}
```

### 错误响应

当抛出异常时，全局异常处理器会自动转换为标准格式：

```json
{
  "code": 40004,
  "message": "用户不存在",
  "data": null,
  "success": false,
  "timestamp": "2024-01-01T12:00:00",
  "traceId": "abc123"
}
```

## 错误码规范

错误码采用 5 位数字格式：

- **200**: 成功
- **4xxxx**: 客户端错误
  - **401xx**: 参数校验错误
  - **402xx**: 认证授权错误
- **5xxxx**: 服务端错误
  - **501xx**: 数据库错误
  - **502xx**: 外部服务错误
- **6xxxx**: 业务错误
  - **601xx**: 用户相关错误
  - **602xx**: 订单相关错误

## 最佳实践

### 1. 异常抛出原则

- **业务异常**: 用于可预期的业务逻辑错误，如用户不存在、余额不足等
- **参数异常**: 用于请求参数校验失败
- **系统异常**: 用于不可预期的系统错误，如数据库连接失败、网络超时等

### 2. 错误消息设计

- 面向用户的消息应该友好、易懂
- 避免暴露系统内部实现细节
- 支持国际化（预留）

### 3. 日志记录

```java
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    public User findById(Long id) {
        try {
            User user = userRepository.findById(id);
            if (user == null) {
                logger.warn("用户不存在: id={}", id);
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            return user;
        } catch (DataAccessException e) {
            logger.error("查询用户失败: id={}", id, e);
            throw new SystemException(ErrorCode.DATABASE_ERROR, "查询用户失败");
        }
    }
}
```

### 4. 参数校验

结合 Bean Validation 使用：

```java
@RestController
public class UserController {
    
    @PostMapping("/users")
    public Result<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        // 参数校验失败会自动抛出 MethodArgumentNotValidException
        // 全局异常处理器会自动处理并返回标准错误响应
        User user = userService.create(request);
        return Result.success(user);
    }
}
```

## 配置选项

在 `application.yml` 中可以配置异常处理行为：

```yaml
exception:
  # 是否为开发环境（显示详细错误信息）
  dev-mode: true
  # 是否记录异常堆栈
  log-stack-trace: true
  # 默认错误消息
  default-message: "系统繁忙，请稍后重试"
```

## 扩展指南

### 1. 添加新的错误码

在 `ErrorCode` 枚举中添加新的错误码：

```java
// 在 ErrorCode.java 中添加
ORDER_NOT_FOUND(60201, "订单不存在"),
ORDER_STATUS_ERROR(60202, "订单状态异常"),
```

### 2. 自定义异常处理

如果需要特殊的异常处理逻辑，可以在具体服务中添加：

```java
@RestControllerAdvice
public class CustomExceptionHandler {
    
    @ExceptionHandler(YourCustomException.class)
    public ResponseEntity<Result<Void>> handleCustomException(YourCustomException e) {
        // 自定义处理逻辑
        return ResponseEntity.ok(Result.error(e.getCode(), e.getMessage()));
    }
}
```

## 注意事项

1. **避免过度使用异常**: 异常应该用于异常情况，不要用于正常的业务流程控制
2. **性能考虑**: 异常创建和抛出有一定性能开销，避免在高频调用的代码中使用
3. **事务回滚**: RuntimeException 会导致事务回滚，确保这是你期望的行为
4. **异常链**: 在捕获并重新抛出异常时，保留原始异常信息

## 版本历史

- **v0.0.1-SNAPSHOT**: 初始版本，提供基础异常处理功能

## 联系方式

如有问题或建议，请联系开发团队。