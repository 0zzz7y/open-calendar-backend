package com.tomaszwnuk.opencalendar.utility.validation

import jakarta.validation.ConstraintViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * The handler for validation errors in REST controllers.
 */
@Suppress("unused")
@RestControllerAdvice
class RestControllerValidationHandler {

    /**
     * Handles validation errors for method arguments.
     *
     * @param exception The exception containing validation errors
     *
     * @return A response entity with a map of validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(exception: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors: Map<String, String> = exception.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        val response: ResponseEntity<Map<String, Any>> = ResponseEntity.badRequest().body(
            mapOf(
                "status" to "error",
                "message" to "Validation failed",
                "errors" to errors
            )
        )

        return response
    }

    /**
     * Handles constraint violations.
     *
     * @param exception The exception containing constraint violations
     *
     * @return A response entity with a map of constraint violations
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolations(exception: ConstraintViolationException): ResponseEntity<Map<String, Any>> {
        val errors: Map<String, String> = exception.constraintViolations.associate {
            it.propertyPath.toString() to (it.message ?: "Invalid value")
        }

        val response: ResponseEntity<Map<String, Any>> = ResponseEntity.badRequest().body(
            mapOf(
                "status" to "error",
                "message" to "Validation failed",
                "errors" to errors
            )
        )

        return response
    }

}
