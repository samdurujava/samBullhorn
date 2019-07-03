package com.joseph.bullhorn;

import org.springframework.data.repository.CrudRepository;

public interface UserList extends CrudRepository<User, Long> {
}
