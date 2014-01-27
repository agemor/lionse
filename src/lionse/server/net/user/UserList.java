package lionse.server.net.user;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserList {
	private Map<String, User> users;
	
	public UserList(){
		users = new HashMap<String, User>();
	}
	
	public void add(User user){
		users.put(user.character.name, user);
	}
	
	public void remove(User user){
		users.remove(user.character.name);
	}
	
	public void sendToAll(String message){
		Iterator<String> iterator = users.keySet().iterator();
		while(iterator.hasNext()){
			User user = users.get(iterator.next());
			user.send(message);
		}		
	}
	
	public String getPacket(){
		StringBuffer buffer = new StringBuffer();
		Iterator<String> iterator = users.keySet().iterator();
		while(iterator.hasNext()){
			User user = users.get(iterator.next());
			buffer.append(user.getPacket() + User.H_M);
		}
		buffer.deleteCharAt(buffer.length()-1);
		return buffer.toString();
	}
	
	public void clear(){
		users.clear();
	}
	
	public int count(){
		return users.size();
	}
}
