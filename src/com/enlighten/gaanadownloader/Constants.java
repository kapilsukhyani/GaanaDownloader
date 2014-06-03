package com.enlighten.gaanadownloader;

public class Constants {
	public static final String APPLICAITON_INITIALIZED = "applciation_initialized";
	private static final String UID = "[UID]";
	public static String PATH_SEPARATOR = "/";

	private static String INTERNAL_FILE_DIR_PATH;
	private static String SOCAT_TCP_INTERCEPT_COMMAND = "socat -v TCP-LISTEN:8081,reuseaddr,debug TCP:streams.gaana.com:80,debug";
	private static final String IPTABLES_ADDRESS_TRANSLATION_COMMAND = "iptables -t nat -A OUTPUT -p 6 --dport 80 -d api.gaana.com -m owner --uid-owner  [UID] -j DNAT --to 127.0.0.1:8081";

	public static void initPaths(String absolutePath) {
		INTERNAL_FILE_DIR_PATH = absolutePath;

	}

	public static void initSocatCommand() {
		SOCAT_TCP_INTERCEPT_COMMAND = INTERNAL_FILE_DIR_PATH + PATH_SEPARATOR
				+ SOCAT_TCP_INTERCEPT_COMMAND;
	}

	public static void initIPTablesCommand(String gaanaUID) {
		IPTABLES_ADDRESS_TRANSLATION_COMMAND.replace(UID, gaanaUID);
	}
}
