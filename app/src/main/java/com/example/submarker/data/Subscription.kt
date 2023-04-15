package com.example.submarker.data

import java.io.Serializable

//class Subscription(
//    userID: String = "",
//    name: String = "",
//    category: String = "",
//    period: Int = 0,
//    periodType: String = "",
//    paymentDate: String = "",
//    price: Double = 0.0,
//) {
//}

class Subscription: Serializable {
    public var userID: String = ""
    public var name: String = ""
    public var category: String = ""
    public var period: String = ""
    public var periodType: String = ""
    public var paymentDate: String = ""
    public var price: String = ""

    constructor() {}

    constructor(userID: String, name: String, category: String, period: String, periodType: String, paymentDate: String, price: String) {
        this.userID = userID
        this.name = name
        this.category = category
        this.period = period
        this.periodType = periodType
        this.paymentDate = paymentDate
        this.price = price
    }
}