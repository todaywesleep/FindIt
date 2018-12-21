package com.papayainc.findit.modal

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var email: String? = "",
    var userSettings: UserSettings = UserSettings()
)