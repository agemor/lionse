package lionse.client.net;

public interface ServerEvent {
	public void connected(boolean succeed);
	public void login(boolean succeed);
	public void join(boolean succeed);
	public void register(boolean succeed);
}
