package edu.fer.drumre.backend.video.popular;

import edu.fer.drumre.backend.location.Country;
import edu.fer.drumre.backend.video.Video;
import io.reactivex.rxjava3.core.Single;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PopularityService {

  private final PopularTodayInCountryRepository popularTodayInCountryRepository;
  private final PopularTodayGloballyRepository popularTodayGloballyRepository;

  public PopularityService(
      PopularTodayInCountryRepository popularTodayInCountryRepository,
      PopularTodayGloballyRepository popularTodayGloballyRepository
  ) {
    this.popularTodayInCountryRepository = popularTodayInCountryRepository;
    this.popularTodayGloballyRepository = popularTodayGloballyRepository;
  }

  @Transactional(readOnly = true)
  public <T extends Video> Single<Page<T>> getMostPopularVideosGloballyToday(
      Class<T> ofType,
      Pageable pageable
  ) {
    return Single.just(
        popularTodayGloballyRepository.findPopularToday(
            ofType,
            pageable
        )
    );
  }

  @Transactional(readOnly = true)
  public <T extends Video> Single<Page<T>> getMostPopularVideosInCountryToday(
      Class<T> ofType,
      Country country,
      Pageable pageable
  ) {
    return Single.just(
        popularTodayInCountryRepository.findPopularByCountryCode(
            ofType,
            country.getCode(),
            pageable
        )
    );
  }
}


