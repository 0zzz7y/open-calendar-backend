package com.tomaszwnuk.dailyassistant.calendar

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CalendarRepository : JpaRepository<Calendar, UUID>
