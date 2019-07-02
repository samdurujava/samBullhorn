package com.joseph.bullhorn;

import org.springframework.data.repository.CrudRepository;

public interface MessageList extends CrudRepository<Message, Long> {
}
