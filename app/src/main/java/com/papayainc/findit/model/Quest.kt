package com.papayainc.findit.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Quest(
    var item_to_search: String = "",
    var reward: Int = 0
)