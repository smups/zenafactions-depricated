package ZenaCraft.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Rank;

public class PlayerCreateRankEvent extends Event{

    private static final HandlerList hl = new HandlerList();

    //attributes
    private final Faction faction;
    private final Player player;
    private final Rank rank;

    public PlayerCreateRankEvent(Faction faction, Player player, Rank rank){
        this.faction = faction;
        this.player = player;
        this.rank = rank;
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
    public Rank getRank(){
        return rank;
    }
}