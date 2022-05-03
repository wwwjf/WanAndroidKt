package com.wwwjf.jetpackdemo.room

import androidx.room.*

@Dao
interface StudentDao {

    @Insert
    fun insert(vararg student: Student)

    @Delete
    fun delete(student: Student)

    @Update
    fun update(student: Student)

    /**
     * 查询一条记录
     */
    @Query("SELECT * FROM Student WHERE name LIKE:name")
    fun findByName(name:String)
}
