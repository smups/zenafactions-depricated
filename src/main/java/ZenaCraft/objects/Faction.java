package ZenaCraft.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import ZenaCraft.objects.loans.*;

import org.bukkit.Bukkit;
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

    //Attributes
    private double balance;
    private double influence;
    private String prefix;
    private Colour colour;
    private HashMap<String,Warp> warps;
    private pItem factionBanner;

    //old rank system
    private String[] ranks;
    private HashMap<UUID,Integer> members;

    //new rank system
    private HashMap<UUID, Rank> member_ranks;
    private List<Rank> faction_ranks;
    private Rank defaultRank;

    //financial stuff
    private double interest = 0.01;
    private double loanlength = 100;
    private List<Loan> runningLoans = new ArrayList<Loan>();
    private List<AvaliableLoan> avaliableLoans = new ArrayList<AvaliableLoan>();

    transient Plugin plugin = App.getPlugin(App.class);

    //deal with outdated warps
    private transient List<Warp> legacywarps;

    public Faction(String Name, Double Balance, Player founder, int newID, Colour newColor){
        name = Name;
        balance = Balance;

        //prevent errors - we have no legacy warps!
        legacywarps = new ArrayList<Warp>();

        //copy the default ranks
        faction_ranks = new ArrayList<Rank>();
        for(Rank defrank : App.defranks) faction_ranks.add(new Rank(defrank));

        defaultRank = faction_ranks.get(2);

        //add the founder
        member_ranks = new HashMap<UUID, Rank>();
        if(founder != null) member_ranks.put(founder.getUniqueId(), faction_ranks.get(0));

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

        //compatibility function for builds before 0.1.15
        if (ranks != null){
            legacywarps = new ArrayList<Warp>();

            Bukkit.getLogger().info(App.zenfac + "Legacy (pre 0.1.15) factions found. Parsing data...");

            faction_ranks = new ArrayList<Rank>();
            member_ranks = new HashMap<UUID, Rank>();

            for (int i = 0; i < ranks.length; i++)
                faction_ranks.add(new Rank(App.defranks.get(i), ranks[i]));

            defaultRank = faction_ranks.get(2);

            //add players to their factions!
            for (Entry e : members.entrySet()){
                int oldrank = (Integer) e.getValue();
                UUID player = (UUID) e.getKey();

                //assign player appropriate new rank:
                member_ranks.put(player, faction_ranks.get(oldrank));
            }

            //put all the legacy warps in the legacy warp container
            warps.values().forEach((warp) -> {legacywarps.add(warp);});

            ranks = null;
            members = null;

        }

        //if there are new perms, add them to the good ranks!
        for (int i = 0; i < 3; i++){
            Rank frank = faction_ranks.get(i);
            App.defranks.get(i).getPerms().forEach( (perm) -> {
                if (!frank.hasPerm(perm)){
                    frank.addPerm(perm);
                }                     
            });
        }
    }

    public List<Warp> getLegacyWarps(){
        return legacywarps;
    }

    public boolean hasLegacyWarps(){
        return !legacywarps.isEmpty();
    }

    public void clearLegacyWarps(){
        legacywarps = null;
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
    public List<Rank> getRanks(){
        return faction_ranks;
    }
    public void setRanks(List<Rank> ranks){
        this.faction_ranks = ranks;
    }
    public void addRank(Rank rank){
        faction_ranks.add(rank);
    }
    public void removeRank(Rank rank){
        if (!faction_ranks.contains(rank)) return;
        faction_ranks.remove(rank);
    }
    public HashMap<UUID,Rank> getMembers(){
        return member_ranks;
    }
    public void setMembers(HashMap<UUID, Rank> newMembers){
        this.member_ranks = newMembers;
    }
    public boolean isMember(Player player){
        return member_ranks.containsKey(player.getUniqueId());
    }
    public void removeMember(UUID player){
        if (member_ranks.containsKey(player)){
            this.member_ranks.remove(player);
        }
    }
    public void addMember(UUID player, Rank rank){
        this.member_ranks.put(player, rank);
    }
    public void changeRank(UUID player, Rank rank){
        this.member_ranks.replace(player, rank);
    }
    public Rank getPlayerRank(Player player){
        return member_ranks.get(player.getUniqueId());
    }
    public Rank getDefaultRank(){
        return defaultRank;
    }

    //faction things
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
    @Nullable
    public void removeWarp(String name, Player player){
        ModifyWarpEvent e = new ModifyWarpEvent(warps.get(name), this, player, false);
        warps.remove(name);
        e.callEvent();
    }
    @Nullable
    public void deleteWarps(Player player){
        for (Entry m : warps.entrySet()){
            ModifyWarpEvent e = new ModifyWarpEvent((Warp) m.getValue(), this, player, false);
            e.callEvent();
        }
        warps.clear();
    }
    @Nullable
    public void addWarp(Location location, String name, Player player){
        Warp w = new Warp(location, name);
        ModifyWarpEvent e = new ModifyWarpEvent(w, this, player, true);
        warps.put(name, w);
        e.callEvent();

        //give all ranks permission
        for (Rank r : faction_ranks) r.addPerm(w.getPerm());
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

    //new rank system

}