package com.tomaszwnuk.opencalendar.authentication

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class TokenBlackList {

    private val _invalidatedTokens: ConcurrentHashMap.KeySetView<String, Boolean> = ConcurrentHashMap.newKeySet()

    fun invalidate(token: String) {
        _invalidatedTokens.add(token)
    }

    fun isInvalid(token: String): Boolean = token in _invalidatedTokens

}
