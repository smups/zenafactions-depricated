package ZenaCraft.objects;

import java.util.HashMap;
import java.util.UUID;

public class Faction {
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
}
