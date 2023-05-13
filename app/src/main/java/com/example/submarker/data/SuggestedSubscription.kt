package com.example.submarker.data

import java.io.Serializable

class SuggestedSubscription: Serializable {
    public var name: String = ""
    public var category: String = ""
    public var period: String = ""
    public var price: Double = 0.0
    public var description: String = ""

    constructor() {}

    constructor(name: String, category: String, period: String, price: Double, description: String) {
        this.name = name
        this.category = category
        this.period = period
        this.price = price
        this.description = description
    }
}