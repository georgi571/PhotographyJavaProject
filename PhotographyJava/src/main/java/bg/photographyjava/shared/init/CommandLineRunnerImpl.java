package bg.photographyjava.shared.init;

import bg.photographyjava.user.service.CountryService;
import bg.photographyjava.user.service.RoleService;
import bg.photographyjava.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final RoleService roleService;
    private final CountryService countryService;
    private final UserService userService;
    private final Environment environment;

    public CommandLineRunnerImpl(RoleService roleService, CountryService countryService, UserService userService, Environment environment) {
        this.roleService = roleService;
        this.countryService = countryService;
        this.userService = userService;
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        this.roleService.seedRoles();
        this.countryService.seedCountries();

        String[] activeProfiles = environment.getActiveProfiles();
        boolean isTestProfileActive = false;
        for (String profile : activeProfiles) {
            if ("test".equals(profile)) {
                isTestProfileActive = true;
                break;
            }
        }

        if (!isTestProfileActive) {
            this.userService.seedUsers();
        }
    }
}
