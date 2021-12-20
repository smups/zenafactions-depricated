package ZenaCraft.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.Faction;
import ZenaCraft.objects.FactionQChunk;

public class AsyncFQCChangeEvent extends Event{
    
    private static final HandlerList hl = new HandlerList();

    private FactionQChunk fqc;
    private Faction f;

    public AsyncFQCChangeEvent(FactionQChunk fqc, Faction f){
        super(true); //async!
        this.fqc = fqc;
        this.f = f;
    }

    @Override
    public HandlerList getHandlers(){
        return hl;
    }

    public static HandlerList getHandlerList(){
        return hl;
    }

    //getters en setters
    public FactionQChunk getFactionQChunk(){
        return this.fqc;
    }
    public Faction getFaction(){
        return this.f;
    }
}
