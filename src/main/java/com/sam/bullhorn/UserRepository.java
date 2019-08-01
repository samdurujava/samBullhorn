package com.sam.bullhorn;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
//    Long countByUsername(String username);
//
//    User findByEmail(String email);
//
//    Long countByEmail(String email);
}
