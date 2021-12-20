package ZenaCraft.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import ZenaCraft.objects.loans.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.events.ModifyWarpEvent;

public class Faction implements Serializable{
    static final long serialVersionUID = 1L;

    //Identifiers
    private final int ID;
    private String name;
    private String[] ranks = {"rank0", "rank1", "rank2"};
    //Attributes
    private double balance;
    private double influence;
    private HashMap<UUID,Integer> members;
    private String prefix;
    private Colour colour;
    private HashMap<String,Warp> warps;
    private pItem factionBanner;

    //financial stuff
    private double interest = 0.01;
    private double loanlength = 100;
    private List<Loan> runningLoans = new ArrayList<Loan>();
    private List<AvaliableLoan> avaliableLoans = new ArrayList<AvaliableLoan>();

    transient Plugin plugin = App.getPlugin(App.class);

    public Faction(String Name, String[] Ranks, Double Balance, HashMap<UUID,Integer> Members, int newID, Colour newColor){
        name = Name;
        ranks = Ranks;
        balance = Balance;
        members = Members;
        influence = 0;
        ID = newID;
        colour = newColor;

        //financial stuff
        interest = plugin.getConfig().getDouble("default interest");
        loanlength = plugin.getConfig().getInt("default loan length");

        warps = new HashMap<String,Warp>();
        updatePrefix();
    }

    /*
    Deze override is nodig omdat anders java niet kan bepalen wanneer
    twee classes hetzelfde zijn, aka hij kan anders deze class niet
    gebruiken als type, en dat willen we wel.
    */

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faction faction = (Faction) o;
        if (name != faction.name) return false;
        if (ID != faction.getID()) return false;
        return true;
    }

    @Override
    public int hashCode(){
        return this.getID();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        //first do the deserelisation
        in.defaultReadObject();

        //now for the special things
        if (warps == null) warps = new HashMap<String,Warp>();
        if (colour == null) colour = new Colour(0xFFFFFF, ChatColor.WHITE);
        if (loanlength <= 0.01) loanlength = 0.01;
        if (avaliableLoans == null) avaliableLoans = new ArrayList<AvaliableLoan>();
        if (runningLoans == null) runningLoans = new ArrayList<Loan>();

        //since loan list is not persistent, we have to load all the loans
        for(Loan loan : runningLoans){
            App.factionIOstuff.addPlayerLoan(loan, loan.getOfflinePlayer());
        }
    }

    private void updatePrefix(){
        prefix = colour.asString() + name;
    }

    //getters en setters
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
        updatePrefix();
    }
    public String[] getRanks(){
        return ranks;
    }
    public void setRanks(String[] newRanks){
        this.ranks = newRanks;
    }
    public double getBalance(){
        return balance;
    }
    public void setBalance(double newBalance){
        this.balance = newBalance;
    }
    public void addBalance(double amount){
        balance += amount;
    }
    public void removeBalance(double amount){
        balance -= amount;
    }
    public double getInfluence(){
        return influence;
    }
    public void setInfluence(double newInfluence){
        this.influence = newInfluence;
    }
    //members
    public HashMap<UUID,Integer> getMembers(){
        return members;
    }
    public void setMembers(HashMap<UUID,Integer> newMembers){
        this.members = newMembers;
    }
    public void removeMember(UUID player){
        if (members.containsKey(player)){
            this.members.remove(player);
        }
    }
    public void addMember(UUID player, int rank){
        this.members.put(player, rank);
    }

    //Ranks
    public void changeRank(UUID player, int rank){
        this.members.replace(player, rank);
    }
    public int getPlayerRank(Player player){
        return members.get(player.getUniqueId());
    }
    public String getPrefix(){
        return prefix;
    }
    public int getID(){
        return ID;
    }
    public Colour getColour(){
        return colour;
    }
    public void setColour(Colour c){
        this.colour = c;
        updatePrefix();
    }

    //warpstuff
    public List<Warp> getWarpList(){
        return new ArrayList<Warp>(warps.values());
    }
    public Warp getWarp(String name){
        return warps.get(name);
    }
    public void removeWarp(String name){
        ModifyWarpEvent e = new ModifyWarpEvent(warps.get(name), this, false);
        warps.remove(name);
        e.callEvent();
    }
    public void deleteWarps(){
        for (Entry m : warps.entrySet()){
            ModifyWarpEvent e = new ModifyWarpEvent((Warp) m.getValue(), this, false);
            e.callEvent();
        }
        warps.clear();
    }
    public void addWarp(Location location, String name){
        Warp w = new Warp(location, name);
        ModifyWarpEvent e = new ModifyWarpEvent(w, this, true);
        warps.put(name, w);
        e.callEvent();
    }
    public boolean hasWarp(String name){
        return warps.containsKey(name);
    }

    //Loans
    public List<Loan> getRunningLoans(){
        return runningLoans;
    }
    public List<AvaliableLoan> getAvaliableLoans(){
        return avaliableLoans;
    }
    public List<Loan> getPlayerLoans(Player p){
        List<Loan> resp = new ArrayList<Loan>();

        for(Loan l : runningLoans){
            if (l.getPlayer().equals(p)) resp.add(l);
        }

        return resp;
    }
    public Loan assignLoan(OfflinePlayer p, AvaliableLoan l){
        avaliableLoans.remove(l);
        Loan loan = new Loan(l, p);
        runningLoans.add(loan);
        return loan;
    }
    public AvaliableLoan createLoan(double amount){
        AvaliableLoan l = new AvaliableLoan(this, amount);
        avaliableLoans.add(l);
        return l;
    }
    public void deleteLoan(Loan l){
        runningLoans.remove(l);
    }
    public void deleteLoan(AvaliableLoan l){
        avaliableLoans.remove(l);
    }
    public void renewLoan(Loan l, double newAmount){
        runningLoans.remove(l);
        AvaliableLoan nl = new AvaliableLoan(this, newAmount);
        assignLoan(l.getOfflinePlayer(), nl);
    }
    public double getLoanLength(){
        return loanlength;
    }
    public void setLoanLength(int loanlength){
        this.loanlength = loanlength;
    }
    public double getInterest(){
        return interest;
    }
    public void setInterest(double interest){
        this.interest = interest;
    }

    //factionbanner
    public pItem getBanner(){
        return this.factionBanner;
    }
    public void setBanner(ItemStack is){
        factionBanner = new pItem(is);
    }
}