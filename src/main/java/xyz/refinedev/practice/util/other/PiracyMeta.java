
package xyz.refinedev.practice.util.other;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.chat.CC;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Scanner;

@RequiredArgsConstructor
public class PiracyMeta {

    private final String authorization = "ee3272d7caa7b8e917a6c17fa02610ce2cc1d4da";

    private final Array plugin;
    private final String productKey;

    private static final String UNKNOWN = "unknown";
    private static final String OS = System.getProperty("os.name").toLowerCase();

    public void verify() {
        this.consoleLog("&7---------------&8[&cRefine-Licenses&8]&7----------------");
        this.consoleLog("&7Verifying your license...");
        this.consoleLog(" ");
        String[] respo = isValid();
        if (respo[0].equals("2") && Boolean.parseBoolean(respo[3])) {
            this.consoleLog("&aLicense valid!");
            this.consoleLog("&7Response: &a" + respo[2]);
            this.consoleLog(" ");
            this.consoleLog("&7Discord: &chttps://dsc.gg/refine");
            this.consoleLog("&7Twitter: &chttps://twitter.com/RefineDev");
            this.consoleLog("&7Contact: &crefinedevelopment@gmail.com");
            this.consoleLog("&7---------------&8[&cRefine-Licenses&8]&7----------------");
            //plugin.getConfigHandler().setOUTDATED(false);
            //plugin.getConfigHandler().setupEssentials();
            TaskUtil.runTimerAsync(new PiracyTask(), 20, TimeUtil.parseTime("60m"));
        } else if (respo[0].equals("3") && Boolean.parseBoolean(respo[3])) {
            this.consoleLog("&aLicense valid!");
            this.consoleLog("&7Response: &a" + respo[2]);
            this.consoleLog(" ");
            this.consoleLog("&eVERSION OUTDATED");
            this.consoleLog("&eYour version: &f" + plugin.getDescription().getVersion());
            this.consoleLog("&aLatest version: &f" + respo[1].split("#")[1]);
            this.consoleLog(" ");
            this.consoleLog("&7Discord: &chttps://dsc.gg/refine");
            this.consoleLog("&7Twitter: &chttps://twitter.com/RefineDev");
            this.consoleLog("&7Contact: &crefinedevelopment@gmail.com");
            this.consoleLog("&7---------------&8[&cRefine-Licenses&8]&7----------------");
            plugin.getConfigHandler().setOUTDATED(true);
            plugin.getConfigHandler().setNEW_VERSION(respo[1].split("#")[1]);
            plugin.getConfigHandler().setupEssentials();
            TaskUtil.runTimerAsync(new PiracyTask(), 20, TimeUtil.parseTime("60m"));
        } else {
            this.consoleLog("&cLicense is not valid!");
            this.consoleLog("&7Reason: &c" + respo[1]);
            this.consoleLog(" ");
            this.consoleLog("&7Discord: &chttps://dsc.gg/refine");
            this.consoleLog("&7Twitter: &chttps://twitter.com/RefineDev");
            this.consoleLog("&7Contact: &crefinedevelopment@gmail.com");
            this.consoleLog("&7---------------&8[&cRefine-Licenses&8]&7----------------");
            System.exit(0);
            Bukkit.shutdown();
        }
    }

    public void hiddenVerify() {
        String[] respo = isValid();
        if (respo[0].equals("3") && Boolean.parseBoolean(respo[3])) {
            this.consoleLog("&eVERSION OUTDATED");
            this.consoleLog("&eYour version: &f" + plugin.getDescription().getVersion());
            this.consoleLog("&aLatest version: &f" + respo[1].split("#")[1]);
            plugin.getConfigHandler().setOUTDATED(true);
            plugin.getConfigHandler().setNEW_VERSION(respo[1].split("#")[1]);
        } else if (!respo[0].equals("2") && !Boolean.parseBoolean(respo[3])) {
            this.consoleLog("&cCould not verify your License, Shutting Down Server in 10 Seconds");
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.isOp()) {
                    player.sendMessage("[" + plugin.getName() + "] &cCould not verify your License, Shutting Down Server in 10 Seconds");
                }
            });
            TaskUtil.runLater(Bukkit::shutdown, 10 * 20L);
        }
    }
    
    public void consoleLog(String string) {
        Bukkit.getConsoleSender().sendMessage(CC.translate( "[" + plugin.getName() + "] " + string));
    }

    private String requestServerHTTPS(String productKey) throws IOException {
        URL url = new URL("https://backend.refinedev.xyz/api/client");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "uLicense");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        String outString = "{\"hwid\":\"password\",\"licensekey\":\"avain\",\"product\":\"NiceCar\",\"version\":\"dogpoop\"}";
        //Align HWID again here if someone tries to spoof it
        outString = outString
                .replaceAll("password", getHWID())
                .replaceAll("avain", productKey)
                .replaceAll("NiceCar", plugin.getName())
                .replaceAll("dogpoop", plugin.getDescription().getVersion());

        byte[] out = outString.getBytes(StandardCharsets.UTF_8);

        con.setRequestProperty("Authorization", this.authorization);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.connect();

        try(OutputStream os = con.getOutputStream()) {
            os.write(out);
        }

        if(!url.getHost().equals(con.getURL().getHost())) return "successful_authentication";

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        }
    }

    public String[] isValid() {
        try {
            String response = requestServerHTTPS(productKey);

            if(!response.contains("{")) {
                return new String[]{"1", "ODD_RESULT", "420"};
            }

            String hash = null;
            String version = null;

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String neekeri = json.get("status_msg").toString();
            String status = json.get("status_overview").toString();

            String statusCode = json.get("status_code").toString();

            if(status.contains("success")) {
                hash = json.get("status_id").toString();
                version = json.get("version").toString();
            }

            if(hash != null && version != null) {
                String[] aa = hash.split("694201337");

                String hashed = aa[0];

                String decoded = new String(Base64.getDecoder().decode(hashed));

                if(!decoded.equals(productKey.substring(0, 2) + productKey.substring(productKey.length() - 2) + authorization.substring(0, 2))) {
                    return new String[]{"1", "FAILED_AUTHENTICATION", statusCode, String.valueOf(false)};
                }

                String time = String.valueOf(Instant.now().getEpochSecond());
                String unix = time.substring(0, time.length() - 2);

                long t = Long.parseLong(unix);
                long hashT = Long.parseLong(aa[1]);

                if (Math.abs(t - hashT) > 1) {
                    return new String[]{"1", "FAILED_AUTHENTICATION", statusCode, String.valueOf(false)};
                }
            }

            int statusLength = status.length();

            if(version != null && !version.equals(plugin.getDescription().getVersion())
                    && status.contains("success") && response.contains("success")
                    && String.valueOf(statusLength).equals("7")) {
                return new String[]{"3", "OUTDATED_VERSION#" + version, statusCode, String.valueOf(true)};
            }

            statusLength = status.length();

            if(!isValidLength(statusLength)) {
                return new String[]{"1", neekeri, statusCode, String.valueOf(false)};
            }

            final boolean valid = status.contains("success") && response.contains("success") && String.valueOf(statusLength).equals("7");

            return new String[]{valid ? "2" : "1", neekeri, statusCode, String.valueOf(valid)};
        } catch (IOException | ParseException ex) {
            if(ex.getMessage().contains("429")) {
                return new String[]{"1", "ERROR", "You are being rate limited because of sending too many requests", String.valueOf(false)};
            }
            ex.printStackTrace();
            return new String[]{"1", "ERROR", ex.getMessage(), String.valueOf(false)};
        }
    }

    public boolean isValidLength(int reps) {
        return reps == 7;
    }

    //Spoofed methods to trick cracker

    public boolean isValidLength22(int reps) {
        return reps == 11;
    }

    public boolean isValidLength222(int reps) {
        return reps == 44;
    }

    public boolean isValidLength2222(int reps) {
        return reps == 48;
    }

    public String getMac() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        StringBuilder sb = new StringBuilder();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            byte[] hardwareAddress = ni.getHardwareAddress();
            if (hardwareAddress != null) {
                for (byte address : hardwareAddress) {
                    sb.append(String.format("%02X", address));
                }
                return sb.toString();
            }
        }

        return null;
    }

    public static String getHWID() {
        try {
            if (isWindows()) {
                return getWindowsIdentifier();
            } else if (isMac()) {
                return getMacOsIdentifier();
            } else if (isLinux()) {
                return getLinuxMacAddress();
            } else {
                return UNKNOWN;
            }
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isMac() {
        return (OS.contains("mac"));
    }

    private static boolean isLinux() {
        return (OS.contains("linux"));
    }

    private static String getLinuxMacAddress() throws FileNotFoundException, NoSuchAlgorithmException {
        File machineId = new File("/var/lib/dbus/machine-id");
        if (!machineId.exists()) {
            machineId = new File("/etc/machine-id");
        }
        if (!machineId.exists()) {
            return UNKNOWN;
        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(machineId);
            String id = scanner.useDelimiter("\\A").next();
            return hexStringify(sha256Hash(id.getBytes()));
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static String getMacOsIdentifier() throws SocketException, NoSuchAlgorithmException {
        NetworkInterface networkInterface = NetworkInterface.getByName("en0");
        byte[] hardwareAddress = networkInterface.getHardwareAddress();
        return hexStringify(sha256Hash(hardwareAddress));
    }

    private static String getWindowsIdentifier() throws IOException, NoSuchAlgorithmException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{"wmic", "csproduct", "get", "UUID"});

        String result = null;
        InputStream is = process.getInputStream();
        Scanner sc = new Scanner(process.getInputStream());
        try {
            while (sc.hasNext()) {
                String next = sc.next();
                if (next.contains("UUID")) {
                    result = sc.next().trim();
                    break;
                }
            }
        } finally {
            is.close();
        }

        return result == null ? UNKNOWN : hexStringify(sha256Hash(result.getBytes()));
    }

    /**
     * Compute the SHA-256 hash of the given byte array
     *
     * @param data the byte array to hash
     * @return the hashed byte array
     */
    public static byte[] sha256Hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(data);
    }

    /**
     * Convert a byte array to its hex-string
     *
     * @param data the byte array to convert
     * @return the hex-string of the byte array
     */
    public static String hexStringify(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte singleByte : data) {
            stringBuilder.append(Integer.toString((singleByte & 0xff) + 0x100, 16).substring(1));
        }

        return stringBuilder.toString();
    }

}
