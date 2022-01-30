package edu.fer.drumre.backend.setup;

import com.google.gson.Gson;
import edu.fer.drumre.backend.location.Country;
import edu.fer.drumre.backend.location.CountryRepository;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class InitializeCountries implements CommandLineRunner {

  private final CountryRepository countryRepository;

  @Autowired
  public InitializeCountries(CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    var loader = Thread.currentThread().getContextClassLoader();
    var countriesInputStream = loader.getResourceAsStream("countries.json");
    if (countriesInputStream == null) {
      throw new FileNotFoundException("Missing countries.json file.");
    }

    var reader = new BufferedReader(new InputStreamReader(countriesInputStream, StandardCharsets.UTF_8));
    var fileContent = reader.lines().collect(Collectors.joining());
    var gson = new Gson();
    var countries = Arrays.asList(gson.fromJson(fileContent, Country[].class));

    var oldCountries = countryRepository.findAll();
    var newCountries = new ArrayList<>(countries);

    newCountries.removeAll(oldCountries);

    if (newCountries.isEmpty()) {
      return;
    }

    countryRepository.saveAll(newCountries);
  }
}
