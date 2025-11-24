package com.basu.vaccineremainder.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basu.vaccineremainder.data.model.Child

@Dao
interface ChildDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: Child)

    @Query("SELECT * FROM children WHERE parentId = :parentId")
    suspend fun getChildrenByParentId(parentId: Int): List<Child>

    @Query("SELECT * FROM children WHERE childId = :childId LIMIT 1")
    suspend fun getChildById(childId: Int): Child?
}
