package edu.fer.drumre.backend.video.show;

import edu.fer.drumre.backend.core.dto.PagingResponse;
import edu.fer.drumre.backend.core.networking.OffsetBasedPageRequest;
import edu.fer.drumre.backend.location.CountryService;
import edu.fer.drumre.backend.user.UserService;
import edu.fer.drumre.backend.video.analytics.AnalyticsService;
import edu.fer.drumre.backend.video.show.dto.ShowDetailsResponse;
import edu.fer.drumre.backend.video.show.dto.ShowResponse;
import io.reactivex.rxjava3.core.Single;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/show")
public class ShowController {

  private static final Logger logger = LoggerFactory.getLogger(ShowController.class);

  private static final String DEFAULT_LIMIT = "10";

  private final ShowMapper showMapper;
  private final ShowService showService;
  private final UserService userService;
  private final AnalyticsService analyticsService;
  private final CountryService countryService;

  public ShowController(
      ShowMapper showMapper,
      ShowService showService,
      UserService userService,
      AnalyticsService analyticsService,
      CountryService countryService
  ) {
    this.showMapper = showMapper;
    this.showService = showService;
    this.userService = userService;
    this.analyticsService = analyticsService;
    this.countryService = countryService;
  }

  @GetMapping("popular")
  public Single<ResponseEntity<PagingResponse<ShowResponse>>> getGloballyPopularShows(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) int limit
  ) {
    return showService.getPopularShows(OffsetBasedPageRequest.of(offset, limit))
        .map(showMapper::localToRemote)
        .map(PagingResponse::create)
        .map(ResponseEntity::ok);
  }

  @GetMapping("popular/{countryCode}")
  public Single<ResponseEntity<PagingResponse<ShowResponse>>> getPopularShowsForCountry(
      @PathVariable("countryCode") String countryCode,
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) int limit
  ) {
    return countryService.getCountryByISO2Code(countryCode)
        .flatMap(country -> showService.getPopularShowsForCountry(
                country,
                OffsetBasedPageRequest.of(offset, limit)
            )
        )
        .map(showMapper::localToRemote)
        .map(PagingResponse::create)
        .map(ResponseEntity::ok);
  }

  @GetMapping("recommendation")
  public Single<ResponseEntity<PagingResponse<ShowResponse>>> getRecommendedShows(
      @RequestHeader(value = "Authorization") String authToken,
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = DEFAULT_LIMIT) int limit
  ) {
    return userService.getUserByToken(authToken)
        .flatMap(user -> showService.getRecommendedShows(
                    user,
                    OffsetBasedPageRequest.of(offset, limit)
                )
                .map(showMapper::localToRemote)
                .map(PagingResponse::create)
                .map(ResponseEntity::ok)
        );
  }

  @GetMapping("details/{showId}/{countryCode}")
  public Single<ResponseEntity<ShowDetailsResponse>> getShowDetails(
      @PathVariable("showId") UUID showUuid,
      @PathVariable(value = "countryCode") String countryCode
  ) {
    return countryService.getCountryByISO2Code(countryCode)
        .flatMap(country -> showService.getShowDetails(showUuid)
            .doOnSuccess(show -> analyticsService.videoClicked(show, country)
                .doOnError(error -> logger.error("Failed to track click for show: " + show.getUuid()))
                .subscribe()
            )
            .map(showMapper::toShowDetails)
            .map(ResponseEntity::ok)
        );
  }

  @GetMapping("details/{showId}")
  public Single<ResponseEntity<ShowDetailsResponse>> getShowDetails(
      @PathVariable("showId") UUID showUuid
  ) {
    return showService.getShowDetails(showUuid)
        .doOnSuccess(show -> analyticsService.videoClicked(show, null)
            .doOnError(error -> logger.error("Failed to track click for show: " + show.getUuid()))
            .subscribe()
        )
        .map(showMapper::toShowDetails)
        .map(ResponseEntity::ok);
  }
}
