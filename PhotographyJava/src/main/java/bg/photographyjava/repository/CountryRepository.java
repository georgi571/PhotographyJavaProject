package bg.photographyjava.repository;

import bg.photographyjava.model.entity.Country;
import bg.photographyjava.model.enums.CountryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CountryRepository extends JpaRepository<Country, UUID> {
    Country findByName(CountryEnum countryEnum);
}
