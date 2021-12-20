package ZenaCraft.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;

public class War implements Serializable{
    static final long serialVersionUID = 45L;

    double warScore;
    Faction defenders;
    Faction attackers;
    HashMap<pChunk,Integer> warzone = new HashMap<pChunk, Integer>();
    int elapsedSeconds;
    int endSecond;
    UUID id;
    String timeRemaining = "24:00:00";

    //transient fields
    transient BossBar defBar;
    transient BossBar atBar;

    public War(Faction Defenders, Faction Attackers){
        warScore = 0.5;
        elapsedSeconds = 0;
        defenders = Defenders;
        attackers = Attackers;
        
        id = UUID.randomUUID();

        //replace the pchunks with normal chunks
        for (Map.Entry mEntry : warzone.entrySet()){
            pChunk pc = (pChunk) mEntry.getKey();
            pc.update();
        }

        endSecond = (int) App.getPlugin(App.class).getConfig().getInt("warDuration (h)")*3600;

        //bossbar things
        createBossBar();
        updateBBWarScore();
        updateBBTitle();

        //add the bossbar to all players
        for (Map.Entry mEntry : defenders.getMembers().entrySet()){
            OfflinePlayer op = Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
            if (op.isOnline()){
                Player p = (Player) op;
                setPlayerBossbar(p);
            }
        }
        for (Map.Entry mEntry : attackers.getMembers().entrySet()){
            OfflinePlayer op = Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
            if (op.isOnline()){
                Player p = (Player) op;
                setPlayerBossbar(p);
            }
        }
    }

    public void update(){
        defenders = App.factionIOstuff.getFaction(defenders.getID());
        attackers = App.factionIOstuff.getFaction(attackers.getID());

        for (Map.Entry mEntry : warzone.entrySet()){
            pChunk pc = (pChunk) mEntry.getKey();
            pc.update();
        }

        createBossBar();
    }

    public void createBossBar(){
        defBar = Bukkit.getServer().createBossBar("dummy", BarColor.WHITE, BarStyle.SEGMENTED_10);
        atBar = Bukkit.getServer().createBossBar("dummy", BarColor.WHITE, BarStyle.SEGMENTED_10);
        updateBBWarScore();
    }
    
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        War war = (War) o;
        if (!id.equals(war.getID())) return false;
        return true;
    }

    private void updateBBWarScore(){
        defBar.setProgress(1 - warScore);
        atBar.setProgress(warScore);
        if (warScore > 0.5){
            defBar.setColor(BarColor.RED);
            atBar.setColor(BarColor.GREEN);
        }
        else if (warScore < 0.5){
            defBar.setColor(BarColor.GREEN);
            atBar.setColor(BarColor.RED);
        }
        else{
            defBar.setColor(BarColor.YELLOW);
            atBar.setColor(BarColor.YELLOW);
        }
    }

    private void updateBBTitle(){
        String def;
        String at;
        if (warScore > 0.5){
            def = ChatColor.RED + "" + ChatColor.BOLD + "[Losing]" + ChatColor.RESET;
            at = ChatColor.GREEN + "" + ChatColor.BOLD + "[Winning]" + ChatColor.RESET;
        }
        else if (warScore < 0.5){
            at = ChatColor.RED + "" + ChatColor.BOLD + "[Losing]" + ChatColor.RESET;
            def = ChatColor.GREEN + "" + ChatColor.BOLD + "[Winning]" + ChatColor.RESET;
        }
        else{
            at = ChatColor.GOLD + "" + ChatColor.BOLD + "[Status Quo]" + ChatColor.RESET;
            def = ChatColor.GOLD + "" + ChatColor.BOLD + "[Status Quo]" + ChatColor.RESET;
        }
        defBar.setTitle(def + " Defensive war against " + attackers.getPrefix() + ChatColor.WHITE + " time remaining: " + ChatColor.BOLD + timeRemaining);
        atBar.setTitle(at + " Offensive war against " + defenders.getPrefix() + ChatColor.WHITE + " time remaining: " + ChatColor.BOLD + timeRemaining);
    }

    //getters en setters
    public synchronized Faction getAttackers(){
        return this.attackers;
    }
    public synchronized Faction getDefenders(){
        return this.defenders;
    }
    public synchronized String getRemainingTimeString(){
        return this.timeRemaining;
    }
    public synchronized void setRemainingTimeString(String s){
        this.timeRemaining = s;
        updateBBTitle();
    }
    public synchronized int getAge(){
        return this.elapsedSeconds;
    }
    public synchronized void setAge(int newTime){
        elapsedSeconds = newTime;
    }
    public synchronized int getDeathTime(){
        return this.endSecond;
    }
    public synchronized void addWarzoneChunk(Chunk wzChunk, int agressor){
        warzone.put(new pChunk(wzChunk), agressor);
    }
    public synchronized void removeWarzoneChunk(Chunk wzChunk){
        warzone.remove(new pChunk(wzChunk));
    }
    public synchronized boolean isWarZone(Chunk wzChunk){
        return warzone.containsKey(new pChunk(wzChunk));
    }
    @Nullable
    public synchronized int getChunkAgressor(Chunk wzChunk){
        if (!isWarZone(wzChunk)) return -1;
        return (int) warzone.get(new pChunk(wzChunk));
    }
    public synchronized HashMap<pChunk, Integer> getWarzone(){
        return this.warzone;
    }
    public synchronized void setWarzone(HashMap<pChunk, Integer> newWZ){
        warzone = newWZ;
    }
    public synchronized void setWarScore(double newWarScore){
        warScore = newWarScore;
        updateBBWarScore();
    }
    public synchronized void addWarScore(double score){
        warScore += score;
        updateBBWarScore();
    }
    public synchronized void removeWarScore(double score){
        if (warScore - score < 0) warScore = 0;
        else warScore -= score;        
        updateBBWarScore();
    }
    public synchronized double getWarScore(){
        return this.warScore;
    }
    public synchronized void setPlayerBossbar(Player player){
        Plugin plugin = App.getPlugin(App.class);
        if(defenders.members.containsKey(player.getUniqueId())){
            defBar.addPlayer(player);
            player.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "War", attackers.getPrefix() + ChatColor.WHITE + " is at war with you!", 10, 50, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 2f);
            player.setMetadata("atWar", new FixedMetadataValue(plugin, true));
        }
        if(attackers.members.containsKey(player.getUniqueId())){
            atBar.addPlayer(player);
            player.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "War", defenders.getPrefix() + ChatColor.WHITE + " is at war with you!", 10, 50, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 2f);
            player.setMetadata("atWar", new FixedMetadataValue(plugin, true));
        }
    }
    public synchronized UUID getID(){
        return this.id;
    }
    public synchronized void setID(UUID id){
        this.id = id;
    }
}