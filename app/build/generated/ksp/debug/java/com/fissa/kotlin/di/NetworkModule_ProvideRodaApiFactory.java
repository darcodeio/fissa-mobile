package com.fissa.kotlin.di;

import com.fissa.kotlin.data.remote.RodaApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class NetworkModule_ProvideRodaApiFactory implements Factory<RodaApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideRodaApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public RodaApi get() {
    return provideRodaApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideRodaApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideRodaApiFactory(retrofitProvider);
  }

  public static RodaApi provideRodaApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideRodaApi(retrofit));
  }
}
