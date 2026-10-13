package com.ff.handler;
import com.ff.exception.BaseException;
import com.ff.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    /**
//     * 捕获业务异常
//     * @param ex
//     * @return
//     */
//    @ExceptionHandler
//    public Result exceptionHandler(BaseException ex){
//        log.error("异常信息：{}", ex.getMessage());
//        return Result.error(ex.getMessage());
//    }
//
//    /**
//     * 捕获 Validation 注解校验异常
//     * 加了这段，@NotBlank @NotNull 才能正常使用！
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Result handleValidException(MethodArgumentNotValidException e) {
//        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
//        return Result.error(msg);
//    }

    /**
     * 业务异常：已知错误，warn 级别
     */
    @ExceptionHandler(BaseException.class)
    public Result<Void> handleBaseException(BaseException e) {
        log.warn("业务异常 [{}]：{}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * @RequestBody 参数校验失败（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("参数错误");
        log.warn("参数校验失败：{}", msg);
        return Result.error(1, msg);
    }

    /**
     * @RequestParam / @PathVariable 校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().iterator().next().getMessage();
        log.warn("参数校验失败：{}", msg);
        return Result.error(1, msg);
    }

    /**
     * 其他参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数绑定失败：{}", msg);
        return Result.error(1, msg);
    }

    /**
     * 未知异常：error 级别，返回模糊信息
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }

}