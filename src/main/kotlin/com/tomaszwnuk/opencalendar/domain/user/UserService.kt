package com.tomaszwnuk.opencalendar.domain.user

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService {

    fun getCurrentUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
        val user = authentication?.principal as? User
            ?: throw IllegalStateException("No authenticated user found in security context.")
        return user.id
    }

    fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.principal as? User
            ?: throw IllegalStateException("No authenticated user found in security context.")
    }

}
