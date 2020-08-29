package com.towerowl.spodify.ext

fun Int.secondsToReadable(): String = String.format("%02d:%02d", this.div(60), this % 60)