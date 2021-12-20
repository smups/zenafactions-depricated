package ZenaCraft.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class pChunk implements Serializable{
    //versie van chunk die wel persistent is
    static final long serialVersionUID = 100L;
    transient Chunk chunk;
    transient Location location;

    private double chunkX;
    private double chunkZ;
    private UUID chunkWorld;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        pChunk pc = (pChunk) o;
        if((chunk != null) && (pc.getChunk() != null)) return chunk.equals(pc.getChunk());
        return location.equals(pc.getLocation());
    }

    @Override
    public int hashCode(){
        int rt = 1;
        rt *= (int) (5*chunkX);
        rt *= (int) (7*chunkZ);
        rt += (int) chunkWorld.hashCode();
        return rt;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        //first do the deserelisation
        in.defaultReadObject();

        //now do the special stuff
        update();
    }

    public pChunk(Chunk c){
        chunk = c;
        chunkWorld = c.getWorld().getUID();
        chunkX = (double) c.getX();
        chunkZ = (double) c.getZ();
        update();
    }

    private void update(){
        if (location == null) location = new Location(Bukkit.getWorld(chunkWorld), chunkX, 0, chunkZ);
        if (chunk == null) chunk = location.getChunk();
    }

    @Nullable
    public Chunk getChunk(){
        return this.chunk;
    }
    public Location getLocation(){
        return this.location;
    }
    
}