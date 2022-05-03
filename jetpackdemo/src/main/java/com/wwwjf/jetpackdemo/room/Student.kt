package com.wwwjf.jetpackdemo.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Student {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uuid")
    var uid:Int = 0

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "age")
    var age: Int? = 0

    @ColumnInfo(name = "sex")
    var sex: Int? = 0


}