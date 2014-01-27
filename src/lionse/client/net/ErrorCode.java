package lionse.client.net;

public class ErrorCode {

	public final static int USER_REQUEST = 0;
	public final static int SESSION_ERROR = 1;
	public final static int SOCKET_ERROR = 2;
	public final static int TOO_MANY_LOGIN_FAILURE = 3;
	public final static int EXCEEDS_MAXUSER = 4;
	public final static int DUPLICATED = 5;

	public final static String CANNOT_FIND_SESSION = "0"; // ERROR CODE
	public final static String LOGIN_FAILED = "1"; // ERROR CODE
	public static final String DB_ERROR = "2";
}
