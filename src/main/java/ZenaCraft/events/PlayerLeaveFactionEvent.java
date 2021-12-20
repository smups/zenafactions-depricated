package ZenaCraft.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.Faction;

public class PlayerLeaveFactionEvent extends Event{

    private static final HandlerList hl = new HandlerList();

    //attributes
    private final Faction faction;
    private final Player player;

    public PlayerLeaveFactionEvent(Faction faction, Player player){
        this.faction = faction;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers(){
        return hl;
    }

    public static HandlerList getHandlerList(){
        return hl;
    }

    //getters en setters
    public Faction getFaction(){
        return faction;
    }
    public Player getPlayer(){
        return player;
    }
}