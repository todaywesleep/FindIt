package com.papayainc.findit.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Label(
    var label: String = ""
)