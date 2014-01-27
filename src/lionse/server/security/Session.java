package lionse.server.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Session {
	private static Set<String> ONLINE_IP = new HashSet<String>();
	private static Map<String, String> SESSION = new HashMap<String, String>();
	
	public static void addAliveIP(String IP){
		ONLINE_IP.add(IP);		
	}
	
	public static boolean isAlive(String IP){
		return ONLINE_IP.contains(IP);
	}
	
	public static void removeDeadIP(String IP){
		ONLINE_IP.remove(IP);
	}
	public static int count(){
		return ONLINE_IP.size();
	}
	
	public static void startSession(String id, String target){
		SESSION.put(id, target);
	}
	
	public static void endSession(String id){
		SESSION.remove(id);
	}
	
	public static String getSession(String id){ // returns character name;
		return SESSION.get(id);
	}
	
	public static void clear(){
		SESSION.clear();
	}
}
