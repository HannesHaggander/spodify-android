package com.towerowl.spodify.ext

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

fun DateTime.toUtc(): DateTime = withZone(DateTimeZone.UTC)