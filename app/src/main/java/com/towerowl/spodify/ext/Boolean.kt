package com.towerowl.spodify.ext

import android.view.View

fun Boolean.asVisibility() = if (this) View.VISIBLE else View.GONE