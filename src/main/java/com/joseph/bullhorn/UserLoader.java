package com.joseph.bullhorn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UserLoader implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... strings) throws Exception {
        Role adminRole = roleRepository.save(new Role("USER"));
        Role userRole = roleRepository.save(new Role("ADMIN"));

        User user = new User("John", "password", true);
        user.setRoles(Arrays.asList(userRole));
        userRepository.save(user);

        user = new User("Admin", "password", true);
        user.setRoles(Arrays.asList(adminRole));
        userRepository.save(user);
    }
}
