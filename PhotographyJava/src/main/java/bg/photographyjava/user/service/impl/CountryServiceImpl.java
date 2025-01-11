package bg.photographyjava.user.service.impl;

import bg.photographyjava.user.model.Country;
import bg.photographyjava.user.property.enums.CountryEnum;
import bg.photographyjava.user.repository.CountryRepository;
import bg.photographyjava.user.service.CountryService;
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
