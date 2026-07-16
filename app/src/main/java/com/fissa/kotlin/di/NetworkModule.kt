package com.fissa.kotlin.di

import com.fissa.kotlin.data.remote.RodaApi
import com.fissa.kotlin.data.repository.FavoritesRepository
import com.fissa.kotlin.data.repository.PlacesRepository
import com.fissa.kotlin.data.repository.RodaFavoritesRepository
import com.fissa.kotlin.data.repository.RodaPlacesRepository
import com.fissa.kotlin.data.repository.RodaStationsRepository
import com.fissa.kotlin.data.repository.RodaTripRepository
import com.fissa.kotlin.data.repository.StationsRepository
import com.fissa.kotlin.data.repository.TripRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * URL du backend fissa_roda — à externaliser en config par build type (dev/staging/prod).
 * IP réseau locale de la machine hébergeant fissa_roda (dev sur téléphone physique via USB/Wi-Fi,
 * même réseau local) ; 10.0.2.2 est l'adresse spéciale émulateur → localhost, inutilisable ici.
 */
private const val RODA_BASE_URL = "http://192.168.100.3:9292/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RODA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRodaApi(retrofit: Retrofit): RodaApi = retrofit.create(RodaApi::class.java)
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindStationsRepository(impl: RodaStationsRepository): StationsRepository

    @Binds
    abstract fun bindPlacesRepository(impl: RodaPlacesRepository): PlacesRepository

    @Binds
    abstract fun bindTripRepository(impl: RodaTripRepository): TripRepository

    @Binds
    abstract fun bindFavoritesRepository(impl: RodaFavoritesRepository): FavoritesRepository
}
