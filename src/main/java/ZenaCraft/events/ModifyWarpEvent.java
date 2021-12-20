package ZenaCraft.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class ModifyWarpEvent extends Event{

    private static final HandlerList hl = new HandlerList();

    private Warp warp;
    private Faction faction;
    private boolean isAlive;

    public ModifyWarpEvent(Warp warp, Faction faction, boolean isAlive){
        super(false); //not async!
        this.warp = warp;
        this.isAlive = isAlive;
        this.faction = faction;
    }

    @Override
    public HandlerList getHandlers(){
        return hl;
    }

    public static HandlerList getHandlerList(){
        return hl;
    }
    
    //getters en setters
    public Warp getWarp(){
        return warp;
    }
    public boolean isAlive(){
        return isAlive;
    }
    public Faction getFaction(){
        return faction;
    }
}