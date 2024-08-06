package de.tert0.lobbycore.portals;

import org.bukkit.Location;

public class Position {
    int x;
    int y;
    int z;

    public Position(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public Position(String raw) {
        String[] data = raw.replace(",", " ").split(" ");
        if(data.length != 3) {
            throw new RuntimeException("Could not parse Position: " + raw);
        }
        this.x = Integer.parseInt(data[0]);
        this.y = Integer.parseInt(data[1]);
        this.z = Integer.parseInt(data[2]);
    }

    public String toString() {
        return "<" + this.x + ", " + this.y + ", " + this.z + ">";
    }
}
