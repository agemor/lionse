package com.lionse.net.user;

import java.util.HashMap;
import java.util.Map;

public class UserList {

	public Map<String, User> users;

	public UserList() {
		users = new HashMap<String, User>();
	}

	public void add(User user) {
		users.put(user.code, user);
	}

	public void remove(String code) {
		users.remove(code);
	}

	public User get(String code) {
		return users.get(code);
	}

	public void clear() {
		users.clear();
	}

	public Map<String, User> getList() {
		return users;
	}

}
