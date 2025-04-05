package com.tomaszwnuk.dailyassistant.domain

import java.time.LocalDateTime

interface Schedulable {

    val date: LocalDateTime

    val recurringPattern: RecurringPattern

}
