package com.papayainc.findit.model

import com.papayainc.findit.constants.DrawerConstants

class DrawerItem {
    var itemName = ""
    var imageRecourse: Int? = null
    var itemType: DrawerConstants = DrawerConstants.NONE
    var isItemSelected: Boolean? = null

    constructor(itemName: String, imageResource: Int, itemType: DrawerConstants, isItemSelected: Boolean?){
        this.itemName = itemName
        this.imageRecourse = imageResource
        this.itemType = itemType
        this.isItemSelected = isItemSelected
    }
}