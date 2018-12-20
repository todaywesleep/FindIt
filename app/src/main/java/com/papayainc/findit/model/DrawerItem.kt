package com.papayainc.findit.model

import com.papayainc.findit.constants.DrawerConstants

class DrawerItem {
    var itemName = ""
    var imageRecourse: Int? = null
    var itemType: DrawerConstants = DrawerConstants.NONE

    constructor(itemName: String, imageResource: Int, itemType: DrawerConstants){
        this.itemName = itemName
        this.imageRecourse = imageResource
        this.itemType = itemType
    }
}