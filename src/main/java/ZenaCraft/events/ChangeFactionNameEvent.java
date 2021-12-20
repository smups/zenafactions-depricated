package ZenaCraft.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.Faction;

public class ChangeFactionNameEvent extends Event{

    private static final HandlerList hl = new HandlerList();

    //attributes
    private final Faction faction;
    private final String oldName;
    private final String newName;

    public ChangeFactionNameEvent(Faction faction, String oldName, String newName){
        this.faction = faction;
        this.oldName = oldName;
        this.newName = newName;
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
    public String oldName(){
        return oldName;
    }
    public String newName(){
        return newName;
    }
    
}