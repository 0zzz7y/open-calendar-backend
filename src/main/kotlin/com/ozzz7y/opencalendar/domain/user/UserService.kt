package com.ozzz7y.opencalendar.domain.user

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

/**
 * The service for user operations.
 */
@Service
class UserService {

    /**
     * Retrieves the unique identifier of the currently authenticated user.
     *
     * @return The unique identifier of the currently authenticated user
     *
     * @throws IllegalStateException If no authenticated user is found in the security context
     */
    fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
        val user = authentication?.principal as? User
            ?: throw IllegalStateException("No authenticated user found in security context.")
        return user.id
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return The currently authenticated user
     *
     * @throws IllegalStateException If no authenticated user is found in the security context
     */
    fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.principal as? User
            ?: throw IllegalStateException("No authenticated user found in security context.")
    }

}
