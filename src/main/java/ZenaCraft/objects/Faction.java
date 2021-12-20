package ZenaCraft.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

    public Faction(String Name, String[] Ranks, Double Balance, HashMap<UUID,Integer> Members, int newID, Colour newColor){
        name = Name;
        ranks = Ranks;
        balance = Balance;
        members = Members;
        influence = 0;
        ID = newID;
        colour = newColor;

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
        warps.remove(name);
    }
    public void deleteWarps(){
        warps.clear();
    }
    public void addWarp(Location location, String name){
        warps.put(name, new Warp(location, name));
    }
    public boolean hasWarp(String name){
        return warps.containsKey(name);
    }
}