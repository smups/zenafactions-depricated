package ZenaCraft.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Rank implements Serializable{
    private static final long serialVersionUID = 100L;

    private final String name;

    private int level;

    private List<String> perms = new ArrayList<String>();

    public Rank(String name){
        this.name = name;
        calcLevel();
    }
    public Rank(Rank that){
        this.name = that.getName() + "-copy";
        this.perms = that.getPerms();
        calcLevel();
    }
    public Rank(Rank that, String name){
        this.name = name;
        this.perms = that.getPerms();
        calcLevel();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rank r = (Rank) o;
        return name.equals(r.getName());
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    private void calcLevel(){
        level = perms.size();
    }

    //getters en setters yo
    public String getName(){
        return name;
    }
    public List<String> getPerms(){
        return perms;
    }
    public void setPerms(List<String> perms){
        this.perms = perms;
        calcLevel();
    }
    public boolean hasPerm(String perm){
        return perms.contains(perm);
    }
    public void addPerm(String perm){
        perms.add(perm);
        calcLevel();
    }
    public void removePerm(String perm){
        if (!perms.contains(perm)) return;
        perms.remove(perm);
        calcLevel();
    }
    public int getLevel(){
        return level;
    }
}