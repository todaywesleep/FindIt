package com.papayainc.findit.model

class ScanResult{
    var label: String
    var percentage: Float

    constructor(label: String, percentage: Float){
        this.label = label
        this.percentage = percentage
    }
}