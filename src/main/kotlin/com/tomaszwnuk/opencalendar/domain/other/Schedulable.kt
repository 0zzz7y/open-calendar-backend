package com.tomaszwnuk.opencalendar.domain.other

import java.time.LocalDateTime

interface Schedulable {

    val startDate: LocalDateTime?

    val endDate: LocalDateTime?

    val recurringPattern: RecurringPattern

}
