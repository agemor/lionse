package com.lionse.net;

import java.util.Map;

import com.lionse.net.user.User;

public interface UserEvent {
	public void arrive(User user);

	public void leave(User user);

	public void load(User me);

	public void userList(Map<String, User> users);

	public void chat(User user);

	public void move(User user);

	public void stop(User user);
}
