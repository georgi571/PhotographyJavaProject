package bg.photographyjava.user.repository;

import bg.photographyjava.user.model.Country;
import bg.photographyjava.user.property.enums.CountryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CountryRepository extends JpaRepository<Country, UUID> {
    Country findByName(CountryEnum countryEnum);
}
