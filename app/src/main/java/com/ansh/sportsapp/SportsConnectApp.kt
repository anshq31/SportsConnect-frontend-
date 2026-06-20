package com.ansh.sportsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

@HiltAndroidApp
class SportsConnectApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this, "", WellKnownTileServer.MapLibre)
    }
}
