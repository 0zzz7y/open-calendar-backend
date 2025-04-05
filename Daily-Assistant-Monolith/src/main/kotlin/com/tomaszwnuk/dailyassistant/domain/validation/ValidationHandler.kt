package com.tomaszwnuk.dailyassistant.domain.validation

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ValidationHandler {

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

}
