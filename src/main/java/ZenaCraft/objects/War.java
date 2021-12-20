package ZenaCraft.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import ZenaCraft.events.WarDeclaredEvent;

public class War implements Serializable{
    static final long serialVersionUID = 100L;

    //persisetent fields
    private UUID id;
    private double warScore;
    private int elapsedSeconds;
    private int endSecond;
    private UUID attackersID;
    private UUID defendersID;
    private HashMap<pChunk, UUID> warzone = new HashMap<pChunk, UUID>();
    private List<String> warzoneMarkers = new ArrayList<String>(); 

    //transient fields
    private transient BossBar defBar;
    private transient BossBar atBar;
    private transient Faction defenders;
    private transient Faction attackers;
    private transient String  timeRemaining;

    public War(Faction Defenders, Faction Attackers){

        //set persistent variables
        id = UUID.randomUUID();
        attackersID = Attackers.getID();
        defendersID = Defenders.getID();
        warScore = 0.5;
        elapsedSeconds = 0;
        endSecond = (int) App.getPlugin(App.class).getConfig().getInt("warDuration (h)")*3600;

        //set transient fields
        defenders = Defenders;
        attackers = Attackers;

        //bossbar things
        createBossBar();
        updateBBWarScore();
        updateBBTitle();

        //call event
        WarDeclaredEvent event = new WarDeclaredEvent(this);
        event.callEvent();
    }

    //Method that is called upon deseralisation, sets the transient fields
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        //first do the deser (load persistent fields)
        in.defaultReadObject();
        
        //now handle the class specific stuff (load transient fields)
        defenders = App.factionIOstuff.getFaction(defendersID);
        attackers = App.factionIOstuff.getFaction(attackersID);

        createBossBar();
        updateBBWarScore();
        updateBBTitle();
    }

    public void createBossBar(){
        defBar = Bukkit.getServer().createBossBar("dummy", BarColor.WHITE, BarStyle.SEGMENTED_10);
        atBar = Bukkit.getServer().createBossBar("dummy", BarColor.WHITE, BarStyle.SEGMENTED_10);

        //add the new bossbar to all players
        for (Map.Entry mEntry : defenders.getMembers().entrySet()){
            OfflinePlayer op = Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
            if (op.isOnline()){
                Player p = (Player) op;
                setPlayerBossBar(p);
            }
        }
        for (Map.Entry mEntry : attackers.getMembers().entrySet()){
            OfflinePlayer op = Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
            if (op.isOnline()){
                Player p = (Player) op;
                setPlayerBossBar(p);
            }
        }
    }
    
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        War war = (War) o;
        if (!id.equals(war.getID())) return false;
        return true;
    }

    @Override
    public int hashCode(){
        return id.hashCode();
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
    public Faction getAttackers(){
        return this.attackers;
    }
    public Faction getDefenders(){
        return this.defenders;
    }

    public String getRemainingTimeString(){
        return this.timeRemaining;
    }
    public void setRemainingTimeString(String s){
        this.timeRemaining = s;
        updateBBTitle();
    }

    public int getAge(){
        return this.elapsedSeconds;
    }
    public void setAge(int newTime){
        elapsedSeconds = newTime;
    }
    public int getDeathTime(){
        return this.endSecond;
    }

    public void addWarzoneChunk(Chunk wzChunk, Faction agressor){
        warzone.put(new pChunk(wzChunk), agressor.getID());
    }
    public List<String> getWarZoneMarkers(){
        return this.warzoneMarkers;
    }
    public void removeWarzoneChunk(Chunk wzChunk){
        warzone.remove(new pChunk(wzChunk));
    }
    public boolean isWarZone(Chunk wzChunk){
        return warzone.containsKey(new pChunk(wzChunk));
    }
    @Nullable
    public Faction getChunkAgressor(Chunk wzChunk){
        if (!isWarZone(wzChunk)) return null;
        UUID agressor = warzone.get(new pChunk(wzChunk));
        return App.factionIOstuff.getFaction(agressor);
    }
    public HashMap<pChunk, UUID> getWarzone(){
        return this.warzone;
    }
    public void setWarzone(HashMap<pChunk, UUID> newWZ){
        warzone = newWZ;
    }

    public void setWarScore(double newWarScore){
        warScore = newWarScore;
        updateBBWarScore();
    }
    public void addWarScore(double score){
        if (warScore + score > 1) warScore = 1;
        else warScore += score;
        updateBBWarScore();
    }
    public void removeWarScore(double score){
        if (warScore - score < 0) warScore = 0;
        else warScore -= score;        
        updateBBWarScore();
    }
    public double getWarScore(){
        return this.warScore;
    }

    public void setPlayerBossBar(Player player){
        Plugin plugin = App.getPlugin(App.class);

        if(defenders.getMembers().containsKey(player.getUniqueId())){
            defBar.addPlayer(player);
            player.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "War", attackers.getPrefix() + ChatColor.WHITE + " is at war with you!", 10, 50, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 2f);
            player.setMetadata("atWar", new FixedMetadataValue(plugin, true));
        }
        if(attackers.getMembers().containsKey(player.getUniqueId())){
            atBar.addPlayer(player);
            player.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "War", defenders.getPrefix() + ChatColor.WHITE + " is at war with you!", 10, 50, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 2f);
            player.setMetadata("atWar", new FixedMetadataValue(plugin, true));
        }
    }
    @Nullable
    public BossBar getPlayerBossBar(Player player){
        if (defBar.getPlayers().contains(player)) return defBar;
        if (atBar.getPlayers().contains(player)) return atBar;
        return null;
    }
    public void removePlayerBossBar(Player player){
        if (defBar.getPlayers().contains(player)) defBar.removePlayer(player);
        if (atBar.getPlayers().contains(player)) atBar.removePlayer(player);
    }
    public void removeAllBossBar(){
        defBar.removeAll();
        atBar.removeAll();
    }

    public UUID getID(){
        return this.id;
    }
}