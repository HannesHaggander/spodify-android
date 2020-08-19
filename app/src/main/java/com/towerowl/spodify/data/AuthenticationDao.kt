package com.towerowl.spodify.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AuthenticationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToken(tokenData: TokenData)

    @Delete
    fun delete(tokenData: TokenData)

    @Query("SELECT * FROM auth_data LIMIT 1")
    fun getStoredToken(): TokenData?

    @Query("SELECT * FROM auth_data LIMIT 1")
    fun liveTokenData(): LiveData<TokenData?>
}