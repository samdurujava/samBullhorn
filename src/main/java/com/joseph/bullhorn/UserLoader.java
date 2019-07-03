package com.joseph.bullhorn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserLoader implements CommandLineRunner {
    @Autowired
    UserList users;

    @Override
    public void run(String... args) throws Exception {
        User user = new User("Admin", "frogfrog");
        users.save(user);
    }
}
