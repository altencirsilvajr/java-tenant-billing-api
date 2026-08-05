package com.altencir.tenantbilling.api;

import com.altencir.tenantbilling.application.ResourceNotFound;
import com.altencir.tenantbilling.application.TenantBoundaryViolation;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(TenantBoundaryViolation.class)
    ProblemDetail boundary(TenantBoundaryViolation exception, HttpServletRequest request) {
        return problem(HttpStatus.FORBIDDEN, "Tenant Boundary Violation", exception.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFound.class)
    ProblemDetail notFound(ResourceNotFound exception, HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Resource Not Found", exception.getMessage(), request);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ProblemDetail badRequest(Exception exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid Request", exception.getMessage(), request);
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail, HttpServletRequest request) {
        var value = ProblemDetail.forStatusAndDetail(status, detail);
        value.setTitle(title); value.setType(URI.create("https://altencir.dev/problems/" + status.value())); value.setInstance(URI.create(request.getRequestURI()));
        return value;
    }
}
