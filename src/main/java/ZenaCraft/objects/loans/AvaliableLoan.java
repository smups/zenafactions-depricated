package ZenaCraft.objects.loans;

import java.io.Serializable;
import java.util.UUID;

import ZenaCraft.objects.Faction;

public class AvaliableLoan implements Serializable {
    static final long serialVersionUID = 100L;

    private final UUID loanID;
    private final double interest;
    private final double amount;
    private final double loanlength;

    public AvaliableLoan(Faction f, double amount){
        loanID = UUID.randomUUID();
        this.amount = amount;
        interest = f.getInterest();
        loanlength = f.getLoanLength();
    }
    public AvaliableLoan(AvaliableLoan that){
        this.loanID = that.getID();
        this.amount = that.getInitAmount();
        this.interest = that.getInterest();
        this.loanlength = that.getLoanLength();
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
}
