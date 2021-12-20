package ZenaCraft.objects.loans;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class AvaliableLoan implements Serializable {
    static final long serialVersionUID = 100L;

    private final UUID loanID;
    private final double interest;
    private final double amount;
    private final double loanlength;
    private final Faction faction;

    public AvaliableLoan(Faction f, double amount){
        loanID = UUID.randomUUID();
        this.amount = amount;
        interest = f.getInterest();
        loanlength = f.getLoanLength();
        faction = f;

        Bukkit.getLogger().info(App.zenfac + "Created loan! ID: " + loanID.toString() +
            " Amount: " + String.valueOf(this.amount) + " Interest: " +
            String.valueOf(interest) + " Duration: " + String.valueOf(loanlength));
    }
    public AvaliableLoan(AvaliableLoan that){
        this.loanID = that.getID();
        this.amount = that.getInitAmount();
        this.interest = that.getInterest();
        this.loanlength = that.getLoanLength();
        this.faction = that.getFaction();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvaliableLoan loan = (AvaliableLoan) o;
        if (!loanID.equals(loan.getID())) return false;
        return true;
    }

    @Override
    public int hashCode(){
        return loanID.hashCode();
    }

    //getters en setters
    public UUID getID(){
        return this.loanID;
    }
    public double getInterest(){
        return this.interest;
    }
    public double getInitAmount(){
        return this.amount;
    }
    public double getLoanLength(){
        return this.loanlength;
    }
    public Faction getFaction(){
        return this.faction;
    }
}
