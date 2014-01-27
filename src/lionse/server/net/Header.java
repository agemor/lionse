package lionse.server.net;

public class Header {
	public final static String SESSION = "s"; // session
	// SESSION: s;session;/ <<----- notice ';/'!

	public final static String LOGIN = "l"; // login!
	// LOGIN: l;id;pw/

	public final static String ERROR = "e"; // error
	// ERROR: e;errorcode;/

	public final static String SERVER = "d"; // server message
	// SERVER: d;status/

	public final static String CHARACTER = "c"; // character data
	// CHARACTER: c;name;lv;exp;mhp;hp;gold;hat;clothes;weapon;stage;x;y;z/

	public final static String LEAVE = "x"; // leave. don't go :(
	// LEAVE: x;name

	public final static String NEW = "n"; // new user! welcome :)
	// NEW: n;name;lv;hat;clothes;weapon;x;y;z/

	public final static String USER_LIST = "u"; // send user list for your
													// synchronizing
	// USELIST: u|name;lv.....|name;lv...../

	public final static String ZAFOK = "z"; // i will die for you, master.
	// ZAFOK: z/

	public final static String BROADCAST = "b"; // custom data
	// BROADCAST: b;data....../

	public final static String MOVE = "m"; // start user movement or change
												// direction
	// MOVE: m;direction;speed/ <----- server receive
	// MOVE: m;name;direction;speed/ <----- client receive

	public final static String STOP = "t"; // stop user movement & record user
												// location
	// STOP: t;x;y;z/ <----- server receive
	// STOP: t;name;x;y;z/ <----- client receive

	public final static String CHANGE = "h"; // change in equipment, for
												// example. add changed item
												// into itemslot, remove
												// changing item from
												// itemslot....
	// CHANGE: h;target(what_is_changed?);no/
	
	
	public final static String SIGNUP = "p";
	// SIGNUP: p;id;pw;charactername;email/
}
