package com.fissa.kotlin.data.repository;

import com.fissa.kotlin.data.remote.RodaApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class RodaStationsRepository_Factory implements Factory<RodaStationsRepository> {
  private final Provider<RodaApi> apiProvider;

  public RodaStationsRepository_Factory(Provider<RodaApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public RodaStationsRepository get() {
    return newInstance(apiProvider.get());
  }

  public static RodaStationsRepository_Factory create(Provider<RodaApi> apiProvider) {
    return new RodaStationsRepository_Factory(apiProvider);
  }

  public static RodaStationsRepository newInstance(RodaApi api) {
    return new RodaStationsRepository(api);
  }
}
