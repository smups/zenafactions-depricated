package ZenaCraft.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Rank;

public class PlayerChangeRankEvent extends Event{

    private static final HandlerList hl = new HandlerList();

    //attributes
    private final Faction faction;
    private final Player actor;
    private final Player target;
    private final Rank oldRank;
    private final Rank newRank;

    public PlayerChangeRankEvent(Faction faction, Player actor, Player target, Rank oldRank, Rank newRank){
        this.faction = faction;
        this.actor = actor;
        this.target = target;
        this.oldRank = oldRank;
        this.newRank = newRank;
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
    public Player getActor(){
        return actor;
    }
    public Player getTarget(){
        return target;
    }
    public Rank getOldRank(){
        return oldRank;
    }
    public Rank getNewRank(){
        return newRank;
    }
}