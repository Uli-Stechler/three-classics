package com.threeclassics.api;

import com.threeclassics.core.IllegalMoveException;
import com.threeclassics.service.GameNotFoundException;
import com.threeclassics.service.StaleVersionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Maps domain errors to RFC 9457 problem details. Framework errors are handled by the base class. */
@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalMoveException.class)
    ProblemDetail illegalMove(IllegalMoveException e) {
        return problem(HttpStatus.BAD_REQUEST, "Illegal move", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail badRequest(IllegalArgumentException e) {
        return problem(HttpStatus.BAD_REQUEST, "Bad request", e.getMessage());
    }

    @ExceptionHandler(GameNotFoundException.class)
    ProblemDetail notFound(GameNotFoundException e) {
        return problem(HttpStatus.NOT_FOUND, "Game not found", e.getMessage());
    }

    @ExceptionHandler(StaleVersionException.class)
    ProblemDetail stale(StaleVersionException e) {
        return problem(HttpStatus.CONFLICT, "Stale game version", e.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ProblemDetail concurrentUpdate(ObjectOptimisticLockingFailureException e) {
        return problem(HttpStatus.CONFLICT, "Stale game version", "The game changed while this move was being made.");
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
