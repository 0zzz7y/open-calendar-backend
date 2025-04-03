package com.tomaszwnuk.dailyassistant.domain

import java.time.LocalDateTime

interface Schedulable {

    val title: String

    val description: String?

    val date: LocalDateTime

    val recurringPattern: RecurringPattern
}
