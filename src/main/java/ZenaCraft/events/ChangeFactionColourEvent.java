package ZenaCraft.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.Colour;
import ZenaCraft.objects.Faction;

public class ChangeFactionColourEvent extends Event{
    
    private static final HandlerList hl = new HandlerList();

    //attributes
    private final Faction faction;
    private final Colour oldColour;
    private final Colour newColour;

    public ChangeFactionColourEvent(Faction faction, Colour oldColour, Colour newColour){
        this.faction = faction;
        this.oldColour = oldColour;
        this.newColour = newColour;
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
    public Colour getOldColour(){
        return oldColour;
    }
    public Colour getNewColour(){
        return newColour;
    }
}
