package net.iryndin.tgmsgsender.util

import java.time.LocalDateTime
import java.time.ZoneOffset

fun ldt2millis(ldt: LocalDateTime): Long = ldt.toInstant(ZoneOffset.UTC).toEpochMilli()