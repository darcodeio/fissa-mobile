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
public final class RodaPlacesRepository_Factory implements Factory<RodaPlacesRepository> {
  private final Provider<RodaApi> apiProvider;

  public RodaPlacesRepository_Factory(Provider<RodaApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public RodaPlacesRepository get() {
    return newInstance(apiProvider.get());
  }

  public static RodaPlacesRepository_Factory create(Provider<RodaApi> apiProvider) {
    return new RodaPlacesRepository_Factory(apiProvider);
  }

  public static RodaPlacesRepository newInstance(RodaApi api) {
    return new RodaPlacesRepository(api);
  }
}
