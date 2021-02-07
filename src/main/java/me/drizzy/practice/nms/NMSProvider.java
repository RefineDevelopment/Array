package me.drizzy.practice.nms;

import me.drizzy.practice.Array;
import me.drizzy.practice.nms.access.NMSAccess;
import me.drizzy.practice.nms.access.NMS_1_7_R4;
import me.drizzy.practice.nms.access.NMS_1_8_R3;
import org.bukkit.Bukkit;

public class NMSProvider {

	private NMSAccess access;
	private String version;
	public boolean laterThan1_8, versionHasNoItemIDs;
	
	public void setup() {
		switch(version = Bukkit.getServer().getClass().getPackage()
				.getName().substring(23)) {
		case "v1_7_R4":
			access = new NMS_1_7_R4();
			break;
		case "v1_8_R3":
			access = new NMS_1_8_R3();
			break;
		default:
			break;
		}
		if(access != null) {
			Array.logger("&aVersion supported! (" + version + ")");
			if(!version.contains("v1_7_R") && !version.contains("v1_8_R")) {
				laterThan1_8 = true;
			}
		}
		else {
			Array.logger("&cVersion is not supported! (" + version + ")");
		}
	}
	
	public NMSAccess getAccess() {
		return access;
	}
	
	public String getVersion() {
		return version;
	}
}
