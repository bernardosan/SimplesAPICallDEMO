package com.example.simplesapicalldemo

data class ResponseData (
    val id: Int,
    val name: String,
    val datalist: List<DataListDetail>
    )

data class DataListDetail(
    val id: Int,
    val name: String
)
