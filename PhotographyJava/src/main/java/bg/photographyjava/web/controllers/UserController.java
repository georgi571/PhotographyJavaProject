package bg.photographyjava.web.controllers;

import bg.photographyjava.entity.dto.RegisterInfoDTO;
import bg.photographyjava.entity.enums.CountryEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // Send all country names to the FE

    @GetMapping("register")
    public RegisterInfoDTO getRegistrationInfo() {
        List<String> countries = CountryEnum.getCountryNames();

        return new RegisterInfoDTO(countries);
    }
}
