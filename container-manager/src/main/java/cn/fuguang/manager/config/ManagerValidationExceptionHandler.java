package cn.fuguang.manager.config;

import cn.fuguang.web.BaseResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice(basePackages = "cn.fuguang.manager.controller")
public class ManagerValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResult<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return BaseResult.fail(firstFieldError(e.getBindingResult().getFieldError()));
    }

    @ExceptionHandler(BindException.class)
    public BaseResult<Void> handleBindException(BindException e) {
        return BaseResult.fail(firstFieldError(e.getBindingResult().getFieldError()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResult<Void> handleConstraintViolation(ConstraintViolationException e) {
        if (e.getConstraintViolations() == null || e.getConstraintViolations().isEmpty()) {
            return BaseResult.fail("参数校验失败");
        }
        ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();
        return BaseResult.fail(violation.getMessage());
    }

    private String firstFieldError(FieldError fieldError) {
        // 2026-05-31: 管理端参数校验统一返回业务失败码，避免前端收到默认异常页。
        if (fieldError == null) {
            return "参数校验失败";
        }
        return fieldError.getDefaultMessage() == null ? "参数校验失败" : fieldError.getDefaultMessage();
    }
}
