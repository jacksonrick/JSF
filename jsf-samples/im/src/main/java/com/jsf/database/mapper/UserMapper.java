package com.jsf.database.mapper;

import com.jsf.database.model.ChatUser;

/**
 * UserMapper Interface
 *
 * @date 2020年07月30日 上午 10:42:59
 * @author jfxu
 */
public interface UserMapper {

	String findNameById(Long id);

    ChatUser findById(Long id);
}