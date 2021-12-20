package ZenaCraft.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Road implements Serializable{
    //Wegen boys
    static final long serialVersionUID = 100L;

    private double[] X;
    private double[] Z;
    private UUID wUuid;
    private final UUID id;

    transient Location corner1;
    transient Location corner2;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Road r = (Road) o;
        return r.getID().equals(this.getID());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        //first do the deserelisation
        in.defaultReadObject();

        //now do the special stuff
        corner1 = new Location(Bukkit.getWorld(wUuid), X[0], 0, Z[0]);
        corner2 = new Location(Bukkit.getWorld(wUuid), X[1], 0, Z[1]);
    }

    public Road(Location corner1, Location corner2){
        this.corner1 = corner1;
        this.corner2 = corner2;

        this.X = new double[] {corner1.getX(), corner2.getX()};
        this.Z = new double[] {corner1.getZ(), corner2.getZ()};

        this.wUuid = UUID.randomUUID();
        this.id = UUID.randomUUID();
    }
    
    //getters en setters
    public UUID getID(){
        return this.id;
    }
}
