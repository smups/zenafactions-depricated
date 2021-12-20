package ZenaCraft.objects.loans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import net.milkbowl.vault.economy.Economy;

public class Loan extends AvaliableLoan{
    static final long serialVersionUID = 100L;

    private final LocalDateTime loanInit;
    private final LocalDateTime expiredate;
    private final UUID player;

    private transient Timer t;


    public Loan(AvaliableLoan l, OfflinePlayer p){
        //assigns Avaliableloan to player

        //copy l
        super(l);

        //now set the loan only stuff
        player = p.getUniqueId();
        loanInit = LocalDateTime.now();
        expiredate = LocalDateTime.now();
        double loanLength = l.getLoanLength();
        int hours = (int) loanLength;
        int minutes = (int) (loanLength*60)%60;
        int seconds = (int) (loanLength*3600)%3600;
        expiredate.plusHours(hours);
        expiredate.plusMinutes(minutes);
        expiredate.plusSeconds(seconds);

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
                payLoan();
            }
        }
    }

    public void payLoan(){
        OfflinePlayer op = Bukkit.getPlayer(player);
        Faction f = App.factionIOstuff.getPlayerFaction(op);
        Economy econ = App.getEconomy();

        double finalPay = getCurrentPrice();

        DecimalFormat df = new DecimalFormat("0.00");

        if(econ.has(op, getCurrentPrice())){
            f.deleteLoan(this);
            econ.withdrawPlayer(op, finalPay);
            f.addBalance(finalPay);
         
            if(op.isOnline()){
                Player p = (Player) op;
                p.sendMessage(App.zenfac + ChatColor.RED + "Your loan has expired! " +
                ChatColor.GOLD + "Ƒ" + df.format(finalPay) + ChatColor.RED +
                " has been deducted from your account and given to your creditors: "
                + f.getPrefix());
            }

            return;
        }

        double playerBalance = econ.getBalance(op);

        econ.withdrawPlayer(op, playerBalance);
        f.addBalance(playerBalance);
        f.renewLoan(this, finalPay - playerBalance);

        if(op.isOnline()){
            Player p = (Player) op;
            p.sendMessage(App.zenfac + ChatColor.RED + "You have defaulted on your debt!" +
                " You've automatically taken out a new Loan with " + f.getPrefix() + ChatColor.RED +
                "worht: " + ChatColor.GOLD + "Ƒ" + df.format(finalPay - playerBalance));
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