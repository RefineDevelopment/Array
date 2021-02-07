package me.drizzy.practice.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class Circle {

	public static List<Location> getCircle(Location center, float radius, int amount) {
        List<Location> list = new ArrayList<Location>();
        for(int i = 0; i < amount; i++) {
            double a = 2*Math.PI / amount * i;
            double x = Math.cos(a) * radius;
            double z = Math.sin(a) * radius;
            list.add(center.clone().add(x, 0, z));
        }
        return list;
    }
}
