package me.drizzy.practice.profile.meta;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;

public class EloLeague {

    public static String getDivision(final Profile player) {
        int elo = player.getGlobalElo();
        String division = "";
        if (elo <= 1000) {
            division = CC.GRAY + "Silver V";
        }
        if (elo >= 1000) {
            division = CC.GRAY + "Silver IV";
        }
        if (elo >= 1050) {
            division = CC.GRAY + "Silver III";
        }
        if (elo >= 1075) {
            division = CC.GRAY + "Silver II";
        }
        if (elo >= 1100) {
            division = CC.GRAY + "Silver I";
        }
        if (elo >= 1150) {
            division = CC.YELLOW + "Gold V";
        }
        if (elo >= 1300) {
            division = CC.YELLOW + "Gold IV";
        }
        if (elo >= 1350) {
            division = CC.YELLOW + "Gold III";
        }
        if (elo >= 1400) {
            division = CC.YELLOW + "Gold II";
        }
        if (elo >= 1450) {
            division = CC.YELLOW + "Gold I";
        }
        if (elo >= 1500) {
            division = CC.AQUA + "Platinum V";
        }
        if (elo >= 1600) {
            division = CC.AQUA + "Platinum IV";
        }
        if (elo >= 1700) {
            division = CC.AQUA + "Platinum III";
        }
        if (elo >= 1800) {
            division = CC.AQUA + "Platinum II";
        }
        if (elo >= 1900) {
            division = CC.AQUA + "Platinum I";
        }
        if (elo >= 2000) {
            division = CC.GREEN + "Emerald V";
        }
        if (elo >= 2100) {
            division = CC.GREEN + "Emerald IV";
        }
        if (elo >= 2200) {
            division = CC.GREEN + "Emerald III";
        }
        if (elo >= 2300) {
            division = CC.GREEN + "Emerald II";
        }
        if (elo >= 2400) {
            division = CC.GREEN + "Emerald I";
        }
        if (elo >= 2500) {
            division = CC.BLUE + "Sapphire V";
        }
        if (elo >= 2600) {
            division = CC.BLUE + "Sapphire IV";
        }
        if (elo >= 2700) {
            division = CC.BLUE + "Sapphire III";
        }
        if (elo >= 2800) {
            division = CC.BLUE + "Sapphire II";
        }
        if (elo >= 2900) {
            division = CC.BLUE + "Sapphire I";
        }
        if (elo >= 3000) {
            division = CC.AQUA + "Champion";
        }
        return division;
    }
}
