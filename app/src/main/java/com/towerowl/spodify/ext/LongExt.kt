package com.towerowl.spodify.ext

fun Long.millisToSec(): Long = this.div(1000)

fun Long.millisToMinutes(): Long = this.div(1000).div(60)