package com.example.practica_desarrollomovil

import android.app.Application
import com.example.practica_desarrollomovil.di.AppContainer

class MetamercaApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
