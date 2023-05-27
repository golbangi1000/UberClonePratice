package com.example.uberclone.models

data class DriverInfoModel(
    val firstName : String,
    val lastName : String,
    val phoneNumber : String,
    val rating : Double
) {
    constructor() : this("","","",0.0)
}