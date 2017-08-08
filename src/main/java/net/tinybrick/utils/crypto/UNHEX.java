package net.tinybrick.utils.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UNHEX {
	private static Logger logger = LogManager.getLogger(UNHEX.class);

	public static String hash(String hex) {
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		
		for (int i = 0; i < hex.length() - 1; i += 2) {
			String output = hex.substring(i, (i + 2));
			int decimal = Integer.parseInt(output, 16);
			sb.append((char) decimal);

			temp.append(decimal);
		}

		return sb.toString();
	}
}
