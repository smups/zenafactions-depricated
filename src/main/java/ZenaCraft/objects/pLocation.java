package ZenaCraft.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class pLocation implements Serializable{
    //versie van Location die wel persistent is
    static final long serialVersionUID = 100L;
    transient Location location;

    private double X;
    private double Z;
    private double Y;
    private UUID worldUuid;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        pLocation pl = (pLocation) o;
        return location.equals(pl.getLoc());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        //first do the deserelisation
        in.defaultReadObject();

        //now do the special stuff
        update();
    }

    public pLocation(Location l){
        location = l;
        worldUuid = l.getWorld().getUID();
        X = (double) l.getX();
        Y = (double) l.getY();
        Z = (double) l.getZ();
        update();
    }

    private void update(){
        if (location == null) location = new Location(Bukkit.getWorld(worldUuid), X, Y, Z);
    }

    public Location getLoc(){
        return this.location;
    }
    
}