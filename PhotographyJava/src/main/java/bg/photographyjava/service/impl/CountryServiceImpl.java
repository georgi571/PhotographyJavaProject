package bg.photographyjava.service.impl;

import bg.photographyjava.model.entity.Country;
import bg.photographyjava.model.enums.CountryEnum;
import bg.photographyjava.repository.CountryRepository;
import bg.photographyjava.service.CountryService;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }


    @Override
    public void seedCountries() {
        if (this.countryRepository.count() == 0) {
            for (CountryEnum country : CountryEnum.getCountries()) {
                this.countryRepository.saveAndFlush(new Country(country));
            }
        }
    }
}
