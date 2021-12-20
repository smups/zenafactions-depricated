package ZenaCraft.objects;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class pChunk implements Serializable{
    //versie van chunk die wel persistent is
    static final long serialVersionUID = 42L;
    transient Chunk chunk;
    
    Location location;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        pChunk pc = (pChunk) o;
        if((chunk != null) && (pc.getChunk() != null)) return chunk.equals(pc.getChunk());
        return location.equals(pc.getLocation());
    }

    public pChunk(Chunk c){
        chunk = c;
        update();
    }

    public void update(){
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