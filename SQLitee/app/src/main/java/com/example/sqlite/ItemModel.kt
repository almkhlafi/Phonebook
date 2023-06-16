package com.example.sqlite

import java.util.*

data class ItemModel(
    var id: Int = getAutoId(),
    var name: String = "",
    var phonenumber:Int = 0,
    var image: ByteArray? = null
) {
    companion object {
        fun getAutoId(): Int {
            val random = Random()
            return random.nextInt(100)
        }
    }


}
