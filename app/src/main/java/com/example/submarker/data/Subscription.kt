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
    var userID: String = ""
    var name: String = ""
    var category: String = ""
    var periodType: String = ""
    var paymentDate: String = ""
    var price: String = ""

    constructor() {}

    constructor(userID: String, name: String, category: String, periodType: String, paymentDate: String, price: String) {
        this.userID = userID
        this.name = name
        this.category = category
        this.periodType = periodType
        this.paymentDate = paymentDate
        this.price = price
    }
}