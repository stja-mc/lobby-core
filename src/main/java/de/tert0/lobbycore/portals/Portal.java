package de.tert0.lobbycore.portals;

public class Portal {
    public final Position corner1;
    public final Position corner2;

    private final int min_x, min_y, min_z;
    private final int max_x, max_y, max_z;

    public final String world;
    public final String destination;

    public Portal(Position corner1, Position corner2, String world, String destination) {
        this.corner1 = corner1;
        this.corner2 = corner2;

        this.min_x = Math.min(corner1.x, corner2.x);
        this.min_y = Math.min(corner1.y, corner2.y);
        this.min_z = Math.min(corner1.z, corner2.z);

        this.max_x = Math.max(corner1.x, corner2.x);
        this.max_y = Math.max(corner1.y, corner2.y);
        this.max_z = Math.max(corner1.z, corner2.z);

        this.world = world;
        this.destination = destination;
    }

    public String toString() {
        return "<" + this.world + ", " + this.destination + ", <" + this.corner1.toString() + ", " + this.corner2.toString() + ">>";
    }

    public boolean isColliding(Position position) {
        return (
                min_x <= position.x && max_x >= position.x &&
                        min_y <= position.y && max_y >= position.y &&
                        min_z <= position.z && max_z >= position.z
                );
    }
}
