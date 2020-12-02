package com.example.lebonangle_front.api.category

data class CategoriesJsonItem(
    val id: Int,
    val name: String
){
    override fun toString(): String {
        return name
    }
}