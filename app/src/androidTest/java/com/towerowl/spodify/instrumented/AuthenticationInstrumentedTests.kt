package com.towerowl.spodify.instrumented

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.towerowl.spodify.data.TokenData
import com.towerowl.spodify.database.AppDatabase
import com.towerowl.spodify.di.ContextModule
import com.towerowl.spodify.di.DaggerViewModelsComponent
import com.towerowl.spodify.di.DatabaseModule
import kotlinx.coroutines.runBlocking
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.DateTime
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AuthenticationInstrumentedTests {

    private fun validToken() = TokenData(
        accessToken = "BQBQgrTs6CDVsDe49aqm_y5XTZtjPVbH04JNjQD7IJ05_iB7Ai7F00OFPg7VVhJk20RdkURxy7MqOhyuy-waw_hwqE8qkif1F2X0nQ37Rrrd0Ss5eXJ8NvKvfqJqQdFxddn8F5UCg9LGNteDMuGTiWfK9M-wUQ",
        expiresAt = DateTime.now().plusSeconds(3600)
    )

    private fun invalidToken() = TokenData(
        accessToken = "BQBQgrTs6CDVsDe49aqm_y5XTZtjPVbH04JNjQD7IJ05_iB7Ai7F00OFPg7VVhJk20RdkURxy7MqOhyuy-waw_hwqE8qkif1F2X0nQ37Rrrd0Ss5eXJ8NvKvfqJqQdFxddn8F5UCg9LGNteDMuGTiWfK9M-wUQ",
        expiresAt = DateTime.now().minusSeconds(3600)
    )

    private fun mockViewModelsComponent() = DaggerViewModelsComponent.builder()
        .contextModule(ContextModule(InstrumentationRegistry.getInstrumentation().targetContext))
        .databaseModule(DatabaseModule(memoryDatabase))
        .build()

    private val memoryDatabase by lazy {
        Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()
    }

    @Before
    fun setup() {
        JodaTimeAndroid.init(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @After
    fun teardown() {
        memoryDatabase.clearAllTables()
    }

    @Test
    fun getTokenVerifySaved() = runBlocking {
        val token = validToken()

        // ensure that a valid token is saved to the database and that its the same upon retrieval
        with(mockViewModelsComponent()) {
            authorizationViewModel().storeToken(token)
            Assert.assertEquals(authorizationViewModel().getToken(), token)
        }

    }

    @Test
    fun ensureNonValidTokenIgnored() = runBlocking {
        val token = TokenData(
            id = "",
            accessToken = "",
            expiresAt = DateTime.now()
        )

        // as the token is faulty it should not be saved to the database
        with(mockViewModelsComponent()) {
            authorizationViewModel().storeToken(token)
            Assert.assertNull(authorizationViewModel().getToken())
        }
    }

}