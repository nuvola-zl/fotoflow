package com.ff.handler;
import com.ff.exception.BaseException;
import com.ff.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获 Validation 注解校验异常
     * 加了这段，@NotBlank @NotNull 才能正常使用！
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(msg);
    }

//    @ExceptionHandler
//    public Result  exceptionHandler(SQLIntegrityConstraintViolationException ex)
//    {
//        String message=ex.getMessage();
//        if(message.contains("Duplicate entry"))
//        {
//            String[] split=message.split(" ");
//            String username=split[2];
//            String msg=username+ MessageConstant.ALREADY_EXISTS;
//            return Result.error(msg);}
//        else{
//            return Result.error(MessageConstant.UNKNOWN_ERROR);
//
//        }
//    }
}