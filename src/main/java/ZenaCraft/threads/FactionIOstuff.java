package ZenaCraft.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dynmap.markers.AreaMarker;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.FactionQChunk;

public class FactionIOstuff {
    
    HashMap<String, FactionQChunk> loadedFQChunks= new HashMap<String, FactionQChunk>();
    HashMap<UUID, Integer> playerHashMap = new HashMap<UUID, Integer>();
    HashMap<Integer, Faction> factionHashMap = new HashMap<Integer, Faction>();

    Plugin plugin = App.getPlugin(App.class);

    String player_db;
    String faction_db;
    String zenfac;
    String FQChunk_db;
    String default_fname;
    double player_influence;

    public FactionIOstuff(String player_db_, String faction_db_, String zenfac_, String FQChunk_db_){
        player_db = player_db_;
        faction_db = faction_db_;
        zenfac = zenfac_;
        FQChunk_db = FQChunk_db_;

        default_fname = plugin.getConfig().getString("default faction name");
        player_influence = plugin.getConfig().getDouble("influence per player");        

        new InitDB();

    }

    //+++ GETTERS EN SETTERS +++
    //FQC's
    public void setLoadedFQChunks(HashMap<String, FactionQChunk> FQCMap){
        loadedFQChunks = FQCMap;
    }
    public FactionQChunk getFQC(String name){
        return this.loadedFQChunks.get(name);
    }
    public HashMap<String, FactionQChunk> getLoadedFQChunks(){
        return this.loadedFQChunks;
    }
    public void addLoadedFQChunk(FactionQChunk FQC){
        loadedFQChunks.put(FQC.getName(), FQC);
    }
    public void removeLoadedFQChunk(FactionQChunk FQC){
        loadedFQChunks.remove(FQC.getName());
    }

    //Players
    public void setKnownPlayers(HashMap<UUID, Integer> PHM){
        playerHashMap = PHM;
    }
    public HashMap<UUID, Integer> getKnownPlayers(){
        return this.playerHashMap;
    }
    public void addKnownPlayer(Player player){
        int playerFactionID = player.getMetadata("factionID").get(0).asInt();
        playerHashMap.put(player.getUniqueId(), playerFactionID);
    }
    public void removeKnownPlayer(Player player){
        playerHashMap.remove(player.getUniqueId());
    }
    public boolean isKnownPlayer(Player player){
        return playerHashMap.containsKey(player.getUniqueId());
    }
    public Faction getPlayerFaction(Player player){
        if (!isKnownPlayer(player)) return null;
        return factionHashMap.get(playerHashMap.get(player.getUniqueId()));
    }

    //Factions
    public void setFactionList(HashMap<Integer, Faction> FHM){
        factionHashMap = FHM;
    }
    public HashMap<Integer, Faction> getFactionList(){
        return this.factionHashMap;
    }
    public Faction getFaction(int ID){
        return this.factionHashMap.get(ID);
    }
    public void addFaction(Faction faction){
        factionHashMap.put(faction.getID(), faction);
    }
    public void removeFaction(Faction faction){
        factionHashMap.remove(faction.getID());
    }
    public void addPlayerToFaction(Faction faction, Player player, int rank){
        faction.addMember(player.getUniqueId(), rank);
        faction.setInfluence(faction.getInfluence() + player_influence);
        playerHashMap.put(player.getUniqueId(), faction.getID());
    }
    public void removePlayerFromFaction(Faction faction, Player player){
        faction.removeMember(player.getUniqueId());
        faction.setInfluence(faction.getInfluence() - player_influence);
        playerHashMap.remove(player.getUniqueId());
    }

    // +++ Threading enzo +++
    //IO classes voor DB init en opslaan
    private class SaveDB implements Runnable{
        /*
            Slaat alle databases op in een aparte thread.
        */
        private Thread t;

        public SaveDB(){
            Bukkit.broadcastMessage(zenfac + ChatColor.GRAY + "(maintenance) Saving Databases...");
            this.start();
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            //Save Faction DB
            try{
                FileOutputStream file = new FileOutputStream(faction_db);
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(factionHashMap);
                out.close();
                file.close();
                plugin.getLogger().info(zenfac + "Saved faction data");
            }
            catch (IOException i){
                i.printStackTrace();
            }
    
            //Save Player DB
            try{
                FileOutputStream file = new FileOutputStream(player_db);
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(playerHashMap);
                out.close();
                file.close();
                plugin.getLogger().info(zenfac + "Saved player data");
            }
            catch (IOException i){
                i.printStackTrace();
            }

            //save FQC DB
            for (Map.Entry mEntry : loadedFQChunks.entrySet()){
                FactionQChunk FQC = (FactionQChunk) mEntry.getValue();
                FQC.saveFQChunkData();
            }
        }
    }
    //Method om deze class te starten
    public void saveDB(){
        new SaveDB();
    }

    private class ClaimChunks implements Runnable{
        /*
            Laadt de FQC chunks rondom de player:
            chunks worden in een vierkant om de speler
            heen geladen.
        */
        private Thread t;
        private Player player;
        private Location location;
        private Integer radius;

        @Nullable
        public ClaimChunks(Player player_, Location location_, Integer radius_, Thread waitThread){
            player = player_;
            if (location_ != null) location = location_;
            else location = player.getLocation();
            if (radius_ != null) radius = radius_;
            else radius = 1;

            this.start();

            if (waitThread != null){
                try{
                    waitThread.join();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        public Thread getThread(){
            return this.t;
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            Chunk chunk = location.getChunk();

            for (int i = -1*radius; i <= radius; i++){
                for (int j = -1*radius; j<= radius; j++){
                    int chunkX = chunk.getX();
                    int chunkZ = chunk.getZ();

                    //Check if points within circle
                    if (i*i + j*j >= radius*radius) continue;
                    chunkX += i;
                    chunkZ += j;
                    String FQCName = "X" + String.valueOf(chunkX/100) + "Z" + String.valueOf(chunkZ/100);
                    byte[][] chunkData = getFQC(FQCName).getChunkData();
                    int ownerID = chunkData[Math.abs(chunkX % 100)][Math.abs(chunkZ % 100)];

                    //Check if the chunk is avaliable
                    if (ownerID != -1){
                        player.sendMessage(zenfac + ChatColor.DARK_RED + "This chunk is already claimed!");
                        continue;
                    }

                    //if it is, claim it!
                    byte playerFaction = player.getMetadata("factionID").get(0).asByte();
                    chunkData[Math.abs(chunkX % 100)][Math.abs(chunkZ % 100)] = playerFaction;

                    //now do the dynmap thingies
                    int color = factionHashMap.get((int) playerFaction).getColor();
                    AreaMarker marker = App.getMarkerSet().createAreaMarker(String.valueOf(chunkX) + String.valueOf(chunkZ), player.getMetadata("faction").get(0).asString(), true, player.getWorld().getName(), new double[] {chunkX*16, chunkX*16 + 16}, new double[] {chunkZ*16, chunkZ*16 + 16}, true);
                    marker.setFillStyle(0.1, color);
                    marker.setLineStyle(1, 0.2, color);
                    player.sendMessage(zenfac + "Chunk Claimed!");
                }
            }
        }
    }
    //Method om deze class te starten
    @Nullable
    public void claimChunks(Player player_, Location location_, Integer radius_, Thread waitThread){
        new ClaimChunks(player_, location_, radius_, waitThread);
    }

    private class LoadFQC implements Runnable{
        /*
            Laadt de FQC chunks rondom de player:
            chunks worden in een vierkant om de speler
            heen geladen.
        */
        private Thread t;
        private Player player;
        private Location location;

        @Nullable
        public LoadFQC(Player player_, Location location_){
            player = player_;
            if (location_ != null) location = location_;
            else location = player.getLocation();
            this.start();
        }

        public Thread getThread(){
            return this.t;
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            byte[][] offsetArr = new byte[][] {{-1,1},{0,1},{1,1},{-1,0},{0,0},{1,0},{-1,-1},{0,-1},{1,-1}};

            for (byte[] offset : offsetArr){

                //Calculate FQC
                int X = location.getChunk().getX();
                int Z = location.getChunk().getZ();
        
                String fQchunkName = "X" + String.valueOf(X/100 + offset[0]) + "Z" + String.valueOf(Z/100 + offset[1]);

                //If the FQC is already loaded, add player to online player list and continue
                if (loadedFQChunks.containsKey(fQchunkName)){
                    loadedFQChunks.get(fQchunkName).addOnlinePlayer(player);
                    continue;
                }

                //If the FQC isn't loaded, load it!
                loadedFQChunks.put(fQchunkName, new FactionQChunk(fQchunkName, player, new double[] {location.getX() + offset[0]*1600, location.getZ() + offset[1]*1600}));
            }
        }
    }
    //Method om deze class te starten
    @Nullable
    public Thread loadFQC(Player player, Location location){
        LoadFQC LFQC = new LoadFQC(player, location);
        return LFQC.getThread();
    }

    private class UnloadFQC implements Runnable{
        /*
            Laadt de FQC chunks rondom de player:
            chunks worden in een vierkant om de speler
            heen geladen.
        */
        private Thread t;
        private Player player;
        private Location location;

        @Nullable
        public UnloadFQC(Player player_, Location location_){
            player = player_;
            if (location_ != null) location = location_;
            else location = player.getLocation();
            this.start();
        }

        public Thread getThread(){
            return this.t;
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }

        public void run(){
            byte[][] offsetArr = new byte[][] {{-1,1},{0,1},{1,1},{-1,0},{0,0},{1,0},{-1,-1},{0,-1},{1,-1}};

            for (byte[] offset : offsetArr){

                //Calculate FQC
                int X = location.getChunk().getX();
                int Z = location.getChunk().getZ();
        
                String fQchunkName = "X" + String.valueOf(X/100 + offset[0]) + "Z" + String.valueOf(Z/100 + offset[1]);

                //If the FQC is already unloaded, continue
                if (!loadedFQChunks.containsKey(fQchunkName)) continue;

                FactionQChunk FQC = loadedFQChunks.get(fQchunkName);

                //Check if player is the only player in the FQC. If not, continue
                if (FQC.getOnlinePlayers().size() != 1){
                    FQC.removeOnlinePlayer(player);
                    continue;
                }

                FQC.saveFQChunkData();
                loadedFQChunks.remove(fQchunkName);
            }
        }
    }
    //Method om deze class te starten
    @Nullable
    public Thread unLoadFQC(Player player, Location location){
        UnloadFQC LFQC = new UnloadFQC(player, location);
        return LFQC.getThread();
    }

    //Deze wordt alleen gebruikt in de constructor, dus geen method erbij
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
            plugin.getLogger().info("Starting database init...");
    
            String[] folders = new String[] {"plugins/ZenaFactions/dat", FQChunk_db + "Q1", FQChunk_db + "Q2", FQChunk_db + "Q3", FQChunk_db + "Q4"};
    
            for (String folder_name : folders){
                File dat = new File(folder_name);
    
                if(!dat.exists()){
                    dat.mkdirs();
                    plugin.getLogger().info(zenfac + "No "+ folder_name +" folder found. Making one...");
                }
            }
    
            File players = new File(player_db);
    
            if (!players.exists()){
                try{
                    FileOutputStream file = new FileOutputStream(player_db);
                    ObjectOutputStream out = new ObjectOutputStream(file);
                    HashMap<UUID, String> player_factions = new HashMap<UUID, String>();
                    out.writeObject(player_factions);
                    out.close();
                    file.close();
                    plugin.getLogger().info(zenfac + "Created player data");
                }
                catch (IOException i){
                    i.printStackTrace();
                }
            }
            else{
                try{
                    plugin.getLogger().info(zenfac + "Loading player data...");
                    FileInputStream file = new FileInputStream(player_db);
                    ObjectInputStream in = new ObjectInputStream(file);
                    playerHashMap = (HashMap<UUID, Integer>) in.readObject();
                    in.close();
                    file.close();
                }
                catch (IOException i){
                    i.printStackTrace();
                }
                catch(ClassNotFoundException c){
                    c.printStackTrace();
                }
            }
    
            File factions = new File(faction_db);
    
            if (!factions.exists()){
                try{
                    FileOutputStream file = new FileOutputStream(faction_db);
                    ObjectOutputStream out = new ObjectOutputStream(file);
    
                    HashMap<UUID, Integer> dummyHashMap = new HashMap<UUID, Integer>();
                    String[] defaultRanks = {"Admin", "Staff", "New Player"};
                    Faction defaultFaction = new Faction(default_fname, defaultRanks, 0.0, dummyHashMap, ChatColor.AQUA + default_fname, 0, 0x55FFFF);

                    factionHashMap.put(0, defaultFaction);
    
                    out.writeObject(factionHashMap);
                    out.close();
                    file.close();
                    plugin.getLogger().info(zenfac + "Created faction data");
                }
                catch (IOException i){
                    i.printStackTrace();
                }
                
            }
            else{
                try{
                    plugin.getLogger().info(zenfac + "Loading faction data...");
                    FileInputStream file = new FileInputStream(faction_db);
                    ObjectInputStream in = new ObjectInputStream(file);
                    factionHashMap = (HashMap<Integer, Faction>) in.readObject();
                    in.close();
                    file.close();
    
                    for (Map.Entry mapElement : factionHashMap.entrySet()){
                        Faction value = (Faction) mapElement.getValue();
                        plugin.getLogger().info("Found Faction: " + value.getName());
                    }
                }
                catch (IOException i){
                    i.printStackTrace();
                }
                catch(ClassNotFoundException c){
                    c.printStackTrace();
                }
            }        
            plugin.getLogger().info(zenfac + "Database init finished!");
        }
    }
}