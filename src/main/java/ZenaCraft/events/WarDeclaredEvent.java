package ZenaCraft.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.War;

public class WarDeclaredEvent extends Event{

    private static final HandlerList hl = new HandlerList();

    //attributes
    private final War war;

    public WarDeclaredEvent(War war){
        this.war = war;
    }

    @Override
    public HandlerList getHandlers(){
        return hl;
    }

    public static HandlerList getHandlerList(){
        return hl;
    }

    //getters en setters
    public War getWar(){
        return war;
    }
}