package com.enlighten.gaanadownloader;

public class Constants {
	public static final String APPLICAITON_INITIALIZED = "applciation_initialized";
	private static final String UID = "[UID]";
	public static String PATH_SEPARATOR = "/";
	public static final String BUSYBOX = "busybox";
	public static final String DOWNLOADED_SONG_FILE_NAME = "downloaded_song";
	public static final int STARTED_INTERCEPTING_NOTIFICAITON = 0x101;
	public static final String GAANA_GET_STREAMING_URI_FRAGMENT = "GET /getURLV1.php";
	public static final String HTTP11 = "HTTP/1.1";
	public static final String HTTP_SUCCESSFUL_RESPONSE_CODE = "200 OK";

	private static String INTERNAL_FILE_DIR_PATH;

	public static final int GAANA_ADDRESS_TRANSLATOR = 1;
	public static final int GAANA_INTERCEPTOR = 2;
	public static final int STOP_GAANA_ADDRESS_TRANSLATOR = 3;
	public static final int KILL_SOCAT = 4;

	// Ten minute socat timeout
	public static final int GAANA_INTERCEPTOR_COMMAND_TIMEOUT = 300000;
	public static String GAANA_TCP_API_INTERCEPT_COMMAND = "socat -v TCP-LISTEN:8081,reuseaddr,debug TCP:api.gaana.com:80,debug";
	public static final String SOCAT_KILL_COMMAND = "busybox pkill socat";
	public static String GAANA_API_ADDRESS_TRANSLATION_COMMAND = "iptables -t nat -A OUTPUT -p 6 --dport 80 -d api.gaana.com -m owner --uid-owner  [UID] -j DNAT --to 127.0.0.1:8081";
	public static final String CLEAN_IPTABLES_COMMAND = "iptables -t nat -F OUTPUT";

	public static void initPaths(String absolutePath) {
		INTERNAL_FILE_DIR_PATH = absolutePath;

	}

	public static void initSocatCommand() {
		GAANA_TCP_API_INTERCEPT_COMMAND = INTERNAL_FILE_DIR_PATH
				+ PATH_SEPARATOR + GAANA_TCP_API_INTERCEPT_COMMAND;
	}

	public static void initIPTablesCommand(String gaanaUID) {
		GAANA_API_ADDRESS_TRANSLATION_COMMAND = GAANA_API_ADDRESS_TRANSLATION_COMMAND
				.replace(UID, gaanaUID);
	}
}
