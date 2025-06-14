package com.tomaszwnuk.opencalendar.authentication

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for managing a blacklist of invalidated tokens.
 */
@Service
class TokenBlackListService {

    /**
     * A set of invalidated tokens.
     */
    private val _invalidatedTokens: ConcurrentHashMap.KeySetView<String, Boolean> = ConcurrentHashMap.newKeySet()

    /**
     * Invalidates a token by adding it to the blacklist.
     *
     * @param token The token to invalidate
     */
    fun invalidate(token: String) {
        _invalidatedTokens.add(token)
    }

    /**
     * Checks if a token is invalidated.
     *
     * @param token The token to check
     *
     * @return True if the token is invalidated, false otherwise
     */
    fun isInvalid(token: String): Boolean = token in _invalidatedTokens

}
