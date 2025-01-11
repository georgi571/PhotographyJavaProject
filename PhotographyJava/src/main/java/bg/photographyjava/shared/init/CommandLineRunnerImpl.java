package bg.photographyjava.shared.init;

import bg.photographyjava.user.service.CountryService;
import bg.photographyjava.user.service.RankService;
import bg.photographyjava.user.service.RoleService;
import bg.photographyjava.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final RankService rankService;
    private final RoleService roleService;
    private final CountryService countryService;
    private final UserService userService;

    public CommandLineRunnerImpl(RankService rankService, RoleService roleService, CountryService countryService, UserService userService) {
        this.rankService = rankService;
        this.roleService = roleService;
        this.countryService = countryService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        this.rankService.seedRanks();
        this.roleService.seedRoles();
        this.countryService.seedCountries();
        this.userService.seedUsers();
    }
}
