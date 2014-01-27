package com.lionse.net;

public interface ServerEvent {
	public void login(boolean succeed);
	public void join(boolean succeed);
	public void register(boolean succeed);
}
