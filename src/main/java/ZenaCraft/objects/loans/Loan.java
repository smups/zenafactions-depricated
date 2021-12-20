package ZenaCraft.objects.loans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Loan extends AvaliableLoan{
    static final long serialVersionUID = 100L;

    private final LocalDateTime loanInit;
    private final LocalDateTime expiredate;
    private final UUID player;

    private transient Timer t;

    public Loan(AvaliableLoan l, Player p){
        //assigns Avaliableloan to player

        //copy l
        super(l);

        //now set the loan only stuff
        player = p.getUniqueId();
        loanInit = LocalDateTime.now();
        expiredate = LocalDateTime.now();
        expiredate.plusHours(l.getLoanLength());

        //start the clock, checking if the player has paid (every min or so)!
        t = new Timer();
        t.schedule(new TikTok(), 60*1000); //timer runs every minute
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();

        t = new Timer();
        t.schedule(new TikTok(), 60*1000);
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        t.cancel();
        out.defaultWriteObject();
    }

    private class TikTok extends TimerTask{
        @Override
        public void run(){
            if(expiredate.isAfter(LocalDateTime.now())){
                //do something
            }
        }
    }

    //getters en setters
    public double getCurrentPrice(){
        double hours = ChronoUnit.SECONDS.between(LocalDateTime.now(), loanInit)/60.;
        return Math.pow(getInterest(), hours)*getInitAmount();
    }
    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(player);
    }
    public Player getPlayer(){
        OfflinePlayer op = getOfflinePlayer();
        return op.getPlayer();
    }
}