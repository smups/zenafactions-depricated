package ZenaCraft.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class Faction implements Serializable{
    String name;
    String[] ranks = {"rank0", "rank1", "rank2"};
    Double balance;
    HashMap<UUID,Integer> members;
    String prefix;

    public Faction(String Name, String[] Ranks, Double Balance, HashMap<UUID,Integer> Members, String Prefix){
        name = Name;
        ranks = Ranks;
        balance = Balance;
        members = Members;
        prefix = Prefix;
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
        return true;
    }

    //getters en setters
    public String getName(){
        return name;
    }
    public String[] getRanks(){
        return ranks;
    }
    public void setRanks(String[] newRanks){
        this.ranks = newRanks;
    }
    public Double getBalance(){
        return balance;
    }
    public void setBalance(Double newBalance){
        this.balance = newBalance;
    }
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
    public void changeRank(UUID player, int rank){
        this.members.replace(player, rank);
    }
    public String getPrefix(){
        return prefix;
    }
    public void setPrefix(String newPrefix){
        this.prefix = newPrefix;
    }
}