package ZenaCraft.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class ModifyWarpEvent extends Event{

    private static final HandlerList hl = new HandlerList();

    private final Warp warp;
    private final Faction faction;
    private final Player player;
    private final boolean isAlive;
    private final boolean isNew;

    public ModifyWarpEvent(Warp warp, Faction faction, Player player, boolean isAlive){
        super(false); //not async!
        this.warp = warp;
        this.isAlive = isAlive;
        this.faction = faction;
        this.player = player;
        this.isNew = false;
    }
    public ModifyWarpEvent(Warp warp, Faction faction, Player player, boolean isAlive, boolean isNew){
        super(false);
        this.warp = warp;
        this.isAlive = isAlive;
        this.faction = faction;
        this.player = player;
        this.isNew = isNew;
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
    public Player getPlayer(){
        return player;
    }
    public boolean isNew(){
        return isNew;
    }
}