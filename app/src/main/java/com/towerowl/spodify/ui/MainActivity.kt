package com.towerowl.spodify.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.towerowl.spodify.R
import com.towerowl.spodify.data.TokenData
import com.towerowl.spodify.misc.App
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val navController by lazy { findNavController(R.id.main_nav) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        App.instance()
            .viewModels
            .authorizationViewModel()
            .run {
                handleAuthResponse(requestCode, resultCode, data,
                    onSuccess = { response ->
                        lifecycleScope.launch(IO) {
                            storeToken(TokenData(response.accessToken, response.expiresIn))
                            withContext(Main) { navController.navigate(R.id.nav_goto_home) }
                        }
                    },
                    onError = {
                        //todo
                    }
                )
            }

    }
}