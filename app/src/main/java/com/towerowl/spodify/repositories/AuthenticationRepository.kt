package com.towerowl.spodify.repositories

import androidx.lifecycle.LiveData
import com.towerowl.spodify.data.AuthenticationDao
import com.towerowl.spodify.data.TokenData

class AuthenticationRepository(private val authenticationDao: AuthenticationDao) {

    fun insert(tokenData: TokenData) = authenticationDao.insertToken(tokenData)

    fun delete(tokenData: TokenData) = authenticationDao.delete(tokenData)

    fun getToken(): TokenData? = authenticationDao.getStoredToken()

    fun getTokenLiveData(): LiveData<TokenData?> = authenticationDao.liveTokenData()

}