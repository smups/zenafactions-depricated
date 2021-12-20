package ZenaCraft.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.War;
import ZenaCraft.objects.pChunk;

public class WarThread {
    
    List<War> onGoingWars = new ArrayList<War>();
    List<Timer> tickingTimers = new ArrayList<Timer>();

    Plugin plugin = App.getPlugin(App.class);

    String zenfac = App.zenfac;
    String war_db;

    public WarThread(String War_db){
        war_db = War_db;

        new InitDB();
    }

    //Does the counting
    private class TikTok extends TimerTask{
        private War war;
        private DecimalFormat df = new DecimalFormat("00");

        public TikTok(War wwar){
            war = wwar;
        }

        @Override
        public void run(){
            if (war.getAge() == war.getDeathTime()) endwar(this);
            if (war.getWarScore() > 0.999 || war.getWarScore() < 0.001) endwar(this);
            war.setAge(war.getAge() + 1);
            int remainsec = war.getDeathTime() - war.getAge();
            String remainingTime = df.format(remainsec/3600) + ":" + df.format((remainsec/60)%60) + ":" + df.format(remainsec%60);
            war.setRemainingTimeString(remainingTime);          
        }

        public War getWar(){
            return this.war;
        }
    }

    //Initialise the whole thing
    private class InitDB implements Runnable{
        private Thread t;

        public InitDB(){
            this.start();
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            plugin.getLogger().info("Setting up war database...");

            File db = new File(war_db);
            
            if(!db.exists()){
                try{
                    FileOutputStream file = new FileOutputStream(war_db);
                    ObjectOutputStream out = new ObjectOutputStream(file);
                    out.writeObject(onGoingWars);
                    out.close();
                    file.close();
                    plugin.getLogger().info(zenfac + "Created war data!");
                }
                catch (IOException i){
                    i.printStackTrace();
                }
            }
            else{
                try{
                    plugin.getLogger().info(zenfac + "Loading war data...");
                    FileInputStream file = new FileInputStream(war_db);
                    ObjectInputStream in = new ObjectInputStream(file);
                    onGoingWars = (List<War>) in.readObject();
                    in.close();
                    file.close();

                    //get the clocks going
                    for (War war : onGoingWars){
                        Bukkit.getLogger().info(App.zenfac  + "Found war: " + war.getAttackers().getPrefix() + ChatColor.RESET + " against " + war.getDefenders().getPrefix());
                        setWarMetadata(war, true);

                        Timer t = new Timer();
                        t.schedule(new TikTok(war), 0, 1000);
                        tickingTimers.add(t);
                    }
                }
                catch (IOException i){
                    i.printStackTrace();
                }
                catch (ClassNotFoundException c){
                    c.printStackTrace();
                }
            }
        }
    }

    private class SaveDB implements Runnable{
        /*
            Slaat war database op in een aparte thread.
        */
        private Thread t;

        public SaveDB(){
            this.start();
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            //save nu de hashmap
            try{
                //stop the timers
                for (Timer t : tickingTimers) t.cancel();

                FileOutputStream file = new FileOutputStream(war_db);
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(onGoingWars);
                out.close();
                file.close();
                plugin.getLogger().info(zenfac + "Saved wars");
            }
            catch (IOException i){
                i.printStackTrace();
            }
        }
    }
    //method om deze thread te starten
    public void saveDB(){
        new SaveDB();
    }

    //sadly, this has to happen asynchronously, since there are a lot of loops
    private class StartWar implements Runnable{
        private Faction defenders;
        private Faction attackers;
        private Chunk firstChunk;
        private Thread t;

        public StartWar(Faction Defenders, Faction Attackers, Chunk firstWarzone){
            defenders = Defenders;
            attackers = Attackers;
            firstChunk = firstWarzone;
            this.start();
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            War war = new War(defenders, attackers);
            addWarzone(war, firstChunk, attackers, null);
            setWarMetadata(war, true);
            
            //timer stuff
            Timer t = new Timer();
            t.schedule(new TikTok(war), 0, 1000);
            tickingTimers.add(t);

            onGoingWars.add(war);
        }
    }
    //ding dat deze thread start
    public void startWar(Faction defenders, Faction attackers, Chunk firstWarzone){
        new StartWar(defenders, attackers, firstWarzone);
    }

    private class EndWar implements Runnable{
        Thread t;
        War war;
        int finalscore;

        public EndWar(TikTok tt){
            war = tt.getWar();
            tt.cancel();
            this.start();
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            finalscore = (int) war.getWarScore()*1000;
            war.removeAllBossBar();
            onGoingWars.remove(war);

            Faction victors = null;
            Faction losers = null;

            if (finalscore > 500){
                victors = war.getAttackers();
                losers = war.getDefenders();
            }
            else if (finalscore < 500){
                victors = war.getDefenders();
                losers = war.getAttackers();
            }
            else return;

            for(Map.Entry mEntry : war.getWarzone().entrySet()){
                int facID = (int) mEntry.getValue();
                if (facID != victors.getID()) continue;
                pChunk pc = (pChunk) mEntry.getKey();
                App.factionIOstuff.claimChunks(null, pc.getLocation(), victors, null, null);
            }

            if (victors != null) sendVictoryMessage(victors);
            if (losers != null) sendDefeatMessage(losers);
        }
    }
    public void endwar(TikTok tt){
        new EndWar(tt);
    }

    private void sendVictoryMessage(Faction victors){
        for (Map.Entry mEntry : victors.getMembers().entrySet()){
            OfflinePlayer op = (OfflinePlayer) Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
            if (!op.isOnline()) continue;
            Player p = (Player) op;
            p.playSound(p.getLocation(), Sound.MUSIC_DISC_CAT, 1f, 1f);
            p.sendTitle(ChatColor.GREEN + "Victory!", "your faction gained territory", 10, 50, 20);
        }
    }
    private void sendDefeatMessage(Faction losers){
        for (Map.Entry mEntry : losers.getMembers().entrySet()){
            OfflinePlayer op = (OfflinePlayer) Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
            if (!op.isOnline()) continue;
            Player p = (Player) op;
            p.playSound(p.getLocation(), Sound.MUSIC_DISC_MELLOHI, 1f, 1f);
            p.sendTitle(ChatColor.RED + "Defeat...", "your faction lost territory", 10, 50, 20);
        }
    }

    public void setWarMetadata(War war, boolean atWar){
        for (Map.Entry mEntry : war.getDefenders().getMembers().entrySet()){
            OfflinePlayer op = Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
            if (!op.isOnline()) continue;
            Player p = (Player) op;
            p.setMetadata("atWar", new FixedMetadataValue(plugin, atWar));
        }
        for (Map.Entry mEntry : war.getAttackers().getMembers().entrySet()){
            OfflinePlayer op = Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
            if (!op.isOnline()) continue;
            Player p = (Player) op;
            p.setMetadata("atWar", new FixedMetadataValue(plugin, atWar));
        }
    }

    @Nullable
    public void addWarzone(War war, Chunk chunk, Faction agressor, Player player){
        Chunk c;
        Faction f;
        War w;

        if (chunk == null) c = player.getChunk();
        else c = chunk;
        if (agressor == null) f = App.factionIOstuff.getPlayerFaction(player);
        else f = agressor;
        if (war == null) w = getWarFromFaction(f);
        else w = war;

        if (w.isWarZone(c)){
            if (player != null) player.sendMessage(App.zenfac + ChatColor.RED + "you already claimed this chunk!");
            return;
        }

        w.addWarzoneChunk(c, f.getID());
        if (player != null) player.sendMessage(App.zenfac + "added chunk to war demands!");
    }

    //getters en setters
    @Nullable
    public War getWar(UUID id){
        for (War war : onGoingWars){
            if (war.getID().equals(id)) return war;
        }
        return null;
    }

    @Nullable
    public War getWarFromFaction(Faction faction){
        for (War war : onGoingWars){
            if (war.getAttackers().equals(faction)) return war;
            if (war.getDefenders().equals(faction)) return war;
        }
        return null;
    }

    public List<War> getWars(){
        return this.onGoingWars;
    }
}
