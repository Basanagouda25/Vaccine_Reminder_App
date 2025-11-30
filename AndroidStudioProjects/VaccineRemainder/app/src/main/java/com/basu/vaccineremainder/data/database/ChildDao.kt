package com.basu.vaccineremainder.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basu.vaccineremainder.data.model.Child
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: Child) : Long

    // FIX: The return type must be a Flow of a LIST of children (Flow<List<Child>>)
    // to match the SQL query which can return multiple rows.
    @Query("SELECT * FROM children WHERE parentId = :parentId")
    fun getChildrenByParentId(parentId: Int): Flow<List<Child>>

    @Query("SELECT * FROM children WHERE childId = :childId LIMIT 1")
    suspend fun getChildById(childId: Long): Child?

    // This function is correct.
    @Query("SELECT * FROM children")
    fun getAllChildren(): Flow<List<Child>>

    @Query("SELECT * FROM children WHERE providerId = :providerId")
    fun getChildrenForProvider(providerId: Int): Flow<List<Child>>
    @Query("SELECT * FROM children WHERE providerId = :providerId")
    fun getChildrenByProviderId(providerId: Int): Flow<List<Child>>


}
