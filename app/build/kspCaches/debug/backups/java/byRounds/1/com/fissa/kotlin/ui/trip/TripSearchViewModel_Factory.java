package com.fissa.kotlin.ui.trip;

import com.fissa.kotlin.data.repository.PlacesRepository;
import com.fissa.kotlin.data.repository.TripRepository;
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
public final class TripSearchViewModel_Factory implements Factory<TripSearchViewModel> {
  private final Provider<PlacesRepository> placesRepositoryProvider;

  private final Provider<TripRepository> tripRepositoryProvider;

  public TripSearchViewModel_Factory(Provider<PlacesRepository> placesRepositoryProvider,
      Provider<TripRepository> tripRepositoryProvider) {
    this.placesRepositoryProvider = placesRepositoryProvider;
    this.tripRepositoryProvider = tripRepositoryProvider;
  }

  @Override
  public TripSearchViewModel get() {
    return newInstance(placesRepositoryProvider.get(), tripRepositoryProvider.get());
  }

  public static TripSearchViewModel_Factory create(
      Provider<PlacesRepository> placesRepositoryProvider,
      Provider<TripRepository> tripRepositoryProvider) {
    return new TripSearchViewModel_Factory(placesRepositoryProvider, tripRepositoryProvider);
  }

  public static TripSearchViewModel newInstance(PlacesRepository placesRepository,
      TripRepository tripRepository) {
    return new TripSearchViewModel(placesRepository, tripRepository);
  }
}
