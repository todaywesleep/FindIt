package com.papayainc.findit.constants

class CommonConstants {
    companion object {
        const val MINIMUM_QUEST_REWARD = 1
        const val MAXIMUM_QUEST_REWARD = 30
        const val MAXIMUM_QUESTS_FOR_USER = 5
        const val INITIAL_QUESTS_AMOUNT = 4
        const val emailRegex = ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")

        const val TIME_FORMAT = "hh:mm:ss"
    }
}