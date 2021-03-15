package me.drizzy.practice.array.essentials;

import me.drizzy.practice.Array;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class License {

	private final String licenseKey;
	private final Plugin plugin;
	private final String validationServer;
	private LogType logType = LogType.NORMAL;
	private String securityKey = "af4efaegfage5rgadfga05thxdrizzyHuW8iUhTdIUInjkfF";
	private boolean debug = false;

	public License(String licenseKey, String validationServer, Plugin plugin) {
		this.licenseKey = licenseKey;
		this.plugin = plugin;
		this.validationServer = validationServer;
	}

	public License setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
		return this;
	}

	public License setConsoleLog(LogType logType) {
		this.logType = logType;
		return this;
	}

	public License debug() {
		debug = true;
		return this;
	}

	public boolean register() {
		log(0, "&7&m---------------&8[&bArray-License&8]&7&m----------------");
		log(0, "Connecting to License-Server...");
		ValidationType vt = isValid();
		if (vt == ValidationType.VALID) {
			log(1, "License valid!");
			log(1, "By using this plugin you agree to our TOS!");
			log(1, "Array is made by Drizzy#0278 at https://discord.link/purge");
			log(1, "");
			log(1, "Note: If this is a cracked copy then please don't use it,");
			log(1, "I work very hard to make this plugin affordable and useful!");
			log(0, "&7&m---------------&8[&bArray-License&8]&7&m----------------");
			return true;
		} else {
			log(1, "            &cLicense is NOT valid!");
			log(1, "&7Failed as a result of &b" + vt.toString());
			log(1, "&cPlugin is probably leaked, &4Disabling Array!");
			log(1, "&7Contact Drizzy#0278 for License Issues!");
			log(0, "&7&m---------------&8[&bArray-License&8]&7&m----------------");
			Bukkit.getScheduler().cancelTasks(plugin);
			Bukkit.getPluginManager().disablePlugin(plugin);
			return false;
		}
	}

	public boolean isValidSimple() {
		return (isValid() == ValidationType.VALID);
	}

	private String requestServer(String v1, String v2) throws IOException {
		URL url = new URL(validationServer + "?v1=" + v1 + "&v2=" + v2 + "&pl=" + plugin.getName());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		int responseCode = con.getResponseCode();
		if (debug) {
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
		}

		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			return response.toString();
		}
	}

	public ValidationType isValid() {
		String rand = toBinary(UUID.randomUUID().toString());
		String sKey = toBinary(securityKey);
		String key = toBinary(licenseKey);

		try {
			String response = requestServer(xor(rand, sKey), xor(rand, key));

			if (response.startsWith("<")) {
				log(1, "The License-Server returned an invalid response!");
				log(1, "In most cases this is caused by:");
				log(1, "1) Your Web-Host injects JS into the page (often caused by free hosts)");
				log(1, "2) Your ValidationServer-URL is wrong");
				log(1,
						"SERVER-RESPONSE: " + (response.length() < 150 || debug ? response : response.substring(0, 150) + "..."));
				return ValidationType.PAGE_ERROR;
			}

			try {
				return ValidationType.valueOf(response);
			} catch (IllegalArgumentException exc) {
				String respRand = xor(xor(response, key), sKey);
				if (rand.substring(0, respRand.length()).equals(respRand))
					return ValidationType.VALID;
				else
					return ValidationType.WRONG_RESPONSE;
			}
		} catch (IOException e) {
			if (debug)
				e.printStackTrace();
			return ValidationType.PAGE_ERROR;
		}
	}

	//
	// Cryptographic
	//

	private static String xor(String s1, String s2) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < (Math.min(s1.length(), s2.length())); i++)
			result.append(Byte.parseByte("" + s1.charAt(i)) ^ Byte.parseByte(s2.charAt(i) + ""));
		return result.toString();
	}

	//
	// Enums
	//

	public enum LogType {
		NORMAL, LOW, NONE;
	}

	public enum ValidationType {
		WRONG_RESPONSE, PAGE_ERROR, URL_ERROR, KEY_OUTDATED, KEY_NOT_FOUND, NOT_VALID_IP, INVALID_PLUGIN, VALID;
	}

	//
	// Binary methods
	//

	private String toBinary(String s) {
		byte[] bytes = s.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}
		return binary.toString();
	}

	//
	// Console-Log
	//

	private void log(int type, String message) {
		if (logType == LogType.NONE || (logType == LogType.LOW && type == 0))
			return;
		Array.logger(message);
	}
}
