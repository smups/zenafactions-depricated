package ZenaCraft.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ZenaCraft.objects.Faction;
import ZenaCraft.objects.loans.AvaliableLoan;

public class LoanCreateEvent extends Event{

    private static final HandlerList hl = new HandlerList();

    //attributes
    private final Faction faction;
    private final AvaliableLoan loan;

    public LoanCreateEvent(Faction faction, AvaliableLoan loan){
        this.faction = faction;
        this.loan = loan;
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
    public AvaliableLoan getLoan(){
        return loan;
    }
}