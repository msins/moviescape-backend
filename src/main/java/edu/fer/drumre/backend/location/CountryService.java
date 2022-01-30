package edu.fer.drumre.backend.location;

import edu.fer.drumre.backend.core.exceptions.EntityWithIdNotFound;
import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CountryService {

  private final CountryRepository countryRepository;

  @Autowired
  public CountryService(CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  @Transactional(readOnly = true)
  public Single<Country> getCountryByISO2Code(String code) {
    return Single.create(subscriber -> {
      var countryOptional = countryRepository.findByCode(code.toUpperCase());
      if (countryOptional.isEmpty()) {
        subscriber.onError(new EntityWithIdNotFound(Country.class, code));
        return;
      }

      subscriber.onSuccess(countryOptional.get());
    });
  }
}
