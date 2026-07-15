package com.fissa.kotlin.ui.home;

import com.fissa.kotlin.data.repository.StationsRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<StationsRepository> stationsRepositoryProvider;

  public HomeViewModel_Factory(Provider<StationsRepository> stationsRepositoryProvider) {
    this.stationsRepositoryProvider = stationsRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(stationsRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<StationsRepository> stationsRepositoryProvider) {
    return new HomeViewModel_Factory(stationsRepositoryProvider);
  }

  public static HomeViewModel newInstance(StationsRepository stationsRepository) {
    return new HomeViewModel(stationsRepository);
  }
}
