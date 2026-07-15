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
public final class RodaTripRepository_Factory implements Factory<RodaTripRepository> {
  private final Provider<RodaApi> apiProvider;

  public RodaTripRepository_Factory(Provider<RodaApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public RodaTripRepository get() {
    return newInstance(apiProvider.get());
  }

  public static RodaTripRepository_Factory create(Provider<RodaApi> apiProvider) {
    return new RodaTripRepository_Factory(apiProvider);
  }

  public static RodaTripRepository newInstance(RodaApi api) {
    return new RodaTripRepository(api);
  }
}
