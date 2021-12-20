package ZenaCraft.objects.loans;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
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
        LocalDateTime Init = LocalDateTime.now();
        LocalDateTime expire = Init;

        double loanLength = l.getLoanLength();
        int hours = (int) loanLength;
        int minutes = (int) (loanLength*60)%60;
        int seconds = (int) (loanLength*3600)%3600;
        expire = expire.plusHours(hours);
        expire = expire.plusMinutes(minutes);
        expire = expire.plusSeconds(seconds);

        this.loanInit = Init;
        this.expiredate = expire;

        //add the loan to the player too!
        App.factionIOstuff.addPlayerLoan(this, Bukkit.getOfflinePlayer(player));

        //start the clock, checking if the player has paid (every min or so)!
        t = new Timer();
        t.schedule(new TikTok(), 0, 60*1000); //timer runs every minute

        Bukkit.getLogger().info("Assigned Loan! LoanID: " + getID().toString() +
            " PlayerID: " + player.toString() + " Expiredate: " + expiredate.toString() +
            " InitDate: " + loanInit.toString() + " Loan Length: " + String.valueOf(getLoanLength()) +
            " Amount: " + String.valueOf(getInitAmount()));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();

        t = new Timer();
        t.schedule(new TikTok(), (int) Math.random()*1000, 60*1000);
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        t.cancel();
        out.defaultWriteObject();
    }

    private class TikTok extends TimerTask{
        @Override
        public void run(){
            if(expiredate.isBefore(LocalDateTime.now())){
                //do somethingt
                payLoan();
            }
        }
    }

    public void payLoan(){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        Faction f = App.factionIOstuff.getPlayerFaction(op);
        Economy econ = App.getEconomy();

        double finalPay = getCurrentPrice();

        //remove this loan!
        App.factionIOstuff.removePlayerLoan(this, op);

        if(econ.has(op, getCurrentPrice())){
            f.deleteLoan(this);
            econ.withdrawPlayer(op, finalPay);
            f.addBalance(finalPay);
         
            if(op.isOnline()){
                Player p = (Player) op;
                p.sendMessage(App.zenfac + ChatColor.RED + "Your loan has expired! " +
                App.getCommon().formatMoney(finalPay) + ChatColor.RED +
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
                " You've automatically taken out a new Loan with [" + f.getPrefix() + ChatColor.RED +
                "] worth: " + App.getCommon().formatMoney(finalPay - playerBalance));
        }
    }

    //getters en setters
    public double getCurrentPrice(){
        Duration d = getTimeTillExpire();
        double hours = ( (double) d.toSeconds() )/3600.;
        return Math.pow(1 + getInterest(), hours)*getInitAmount();
    }
    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(player);
    }
    public Player getPlayer(){
        OfflinePlayer op = getOfflinePlayer();
        return op.getPlayer();
    }
    public LocalDateTime getInitTime(){
        return loanInit;
    }
    public LocalDateTime getExpiredate() {
        return expiredate;
    }
    public Duration getTimeTillExpire(){
        return Duration.between(LocalDateTime.now(), expiredate);
    }
}