package com.example.songpaper.di

import android.content.Context
import android.media.session.MediaSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @Provides
    fun provideMediaSessionManager(@ApplicationContext context: Context): MediaSessionManager {
        return context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    }
}
