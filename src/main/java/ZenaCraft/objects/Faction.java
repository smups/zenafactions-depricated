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
import ZenaCraft.events.ChangeFactionColourEvent;
import ZenaCraft.events.ChangeFactionNameEvent;
import ZenaCraft.events.FactionCreateEvent;
import ZenaCraft.events.LoanCreateEvent;
import ZenaCraft.events.ModifyWarpEvent;

public class Faction implements Serializable{
    static final long serialVersionUID = 1L;

    //Identifiers
    private UUID uuid;
    private String name;
    private boolean isDefault;

    //Old ID system
    private int ID = Integer.MIN_VALUE;

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

    public Faction(String Name, Double Balance, Player founder, Colour newColor){

        //set all the basic parameters
        this.uuid = UUID.randomUUID();
        this.influence = 0;
        this.colour = newColor;
        this.name = Name;
        this.balance = Balance;
        updatePrefix();

        //copy the default ranks
        this.faction_ranks = new ArrayList<Rank>();
        for(Rank defrank : App.defranks) faction_ranks.add(new Rank(defrank, defrank.getName()));

        this.defaultRank = faction_ranks.get(2);

        //create member list
        this.member_ranks = new HashMap<UUID, Rank>();

        //move the founder into the new faction
        //critical update!
        if (founder != null)
            App.factionIOstuff.changePlayerFaction(this, founder, faction_ranks.get(0));

        //financial stuff
        interest = plugin.getConfig().getDouble("default interest");
        loanlength = plugin.getConfig().getInt("default loan length");

        this.warps = new HashMap<String,Warp>();

        //call faction creation event
        FactionCreateEvent event = new FactionCreateEvent(this, founder);
        event.callEvent();
    }

    /*
    Deze override is nodig omdat anders java niet kan bepalen wanneer
    twee classes hetzelfde zijn, aka hij kan         //Call the faction creation event
        FactionCreateEvent event = new Facanders deze class niet
    gebruiken als type, en dat willen we wel.
    */

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faction faction = (Faction) o;
        return uuid.equals(faction.getID());
    }

    @Override
    public int hashCode(){
        return this.getID().hashCode();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        //first do the deserilisation
        in.defaultReadObject();

        //now for the special things
        if (this.warps == null) this.warps = new HashMap<String,Warp>();
        if (this.colour == null) this.colour = new Colour(0xFFFFFF, ChatColor.WHITE);
        if (this.loanlength <= 0.01) this.loanlength = 0.01;
        if (this.avaliableLoans == null) this.avaliableLoans = new ArrayList<AvaliableLoan>();
        if (this.runningLoans == null) this.runningLoans = new ArrayList<Loan>();

        //compatibility function for builds before 0.1.16
        if (this.uuid == null){ //
            this.uuid = UUID.randomUUID();

            Bukkit.getLogger().info(App.zenfac + "Legacy (pre 0.1.16) faction found. Parsing data...");
            Bukkit.getLogger().info(App.zenfac + "Assigned new ID (" + uuid.toString() + ") to faction: " + prefix);

            if (ID == 0) this.isDefault = true;
        }

        //compatibility function for builds before 0.1.15
        if (ranks != null){
            legacywarps = new ArrayList<Warp>();

            Bukkit.getLogger().info(App.zenfac + "Legacy (pre 0.1.15) faction found. Parsing data...");

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
            for (String perm : App.defranks.get(i).getPerms()){
                if (!frank.hasPerm(perm)) frank.addPerm(perm);
            }
        }
    }

    public List<Warp> getLegacyWarps(){ 
        return legacywarps;
    }

    public boolean hasLegacyWarps(){
        if (legacywarps == null) return false;
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
    
    public boolean isDefault(){
        return isDefault;
    }
    public void setDefault(boolean isDefault){
        this.isDefault = isDefault;
    }

    public void setName(String name){
        String oldName = name;
        this.name = name;
        updatePrefix();
        ChangeFactionNameEvent event = new ChangeFactionNameEvent(this, oldName, name);
        event.callEvent();
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
        return this.faction_ranks;
    }
    public void setRanks(List<Rank> ranks){
        faction_ranks = ranks;
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
    public UUID getID(){
        return uuid;
    }
    public int getOldID(){
        return ID;
    }
    public Colour getColour(){
        return colour;
    }
    public void setColour(Colour c){
        Colour oldColour = new Colour(this.colour);
        this.colour = c;
        updatePrefix();
        ChangeFactionColourEvent event = new ChangeFactionColourEvent(this, oldColour, c);
        event.callEvent();
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
        warps.put(name, w);

        //give all ranks permission
        for (Rank r : faction_ranks) r.addPerm(w.getPerm());

        //trhow the event
        ModifyWarpEvent e = new ModifyWarpEvent(w, this, player, true);
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
        LoanCreateEvent event = new LoanCreateEvent(this, l);
        event.callEvent();
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