package ZenaCraft.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import ZenaCraft.App;
import ZenaCraft.events.AsyncFQCChangeEvent;
import ZenaCraft.objects.Colour;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.FactionQChunk;
import ZenaCraft.objects.Rank;
import ZenaCraft.objects.loans.Loan;

public class FactionIOstuff {
    
    private HashMap<String, FactionQChunk> loadedFQChunks= new HashMap<String, FactionQChunk>();
    private HashMap<UUID, Integer> playerHashMap = new HashMap<UUID, Integer>();
    private HashMap<UUID, List<Loan>> playerLoanMap = new HashMap<UUID, List<Loan>>();
    private HashMap<Integer, Faction> factionHashMap = new HashMap<Integer, Faction>();

    private Plugin plugin = App.getPlugin(App.class);

    private String player_db;
    private String faction_db;
    private String zenfac;
    private String FQChunk_db;
    private String default_fname;
    private double player_influence;

    //perms
    private String claimchunkperm = "claimchunk";

    public FactionIOstuff(String player_db_, String faction_db_, String zenfac_, String FQChunk_db_){
        player_db = player_db_;
        faction_db = faction_db_;
        zenfac = zenfac_;
        FQChunk_db = FQChunk_db_;

        default_fname = plugin.getConfig().getString("default faction name");
        player_influence = plugin.getConfig().getDouble("influence per player");        

        //register perms
        App.registerPerm(claimchunkperm, 1);

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
        int playerFactionID = getPlayerFaction(player).getID();
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
    public Faction getPlayerFaction(OfflinePlayer player){
        return factionHashMap.get(playerHashMap.get(player.getUniqueId()));
    }
    public String getPlayerPrefix(Player player){
        return getPlayerFaction(player).getPrefix();
    }
    public String getPlayerRank(Player player){
        Rank rankInt = getPlayerFaction(player).getMembers().get(player.getUniqueId());
        return rankInt.getName();
    }
    //Player loans!
    public void setPlayerLoanMap(HashMap<UUID, List<Loan>> map){
        playerLoanMap = map;
    }
    public void setPlayerLoans(List<Loan> loans, Player player){
        if(!playerLoanMap.containsKey(player.getUniqueId())) return;
        playerLoanMap.replace(player.getUniqueId(), loans);
    }
    public void setPlayerLoans(List<Loan> loans, OfflinePlayer player){
        if(!playerLoanMap.containsKey(player.getUniqueId())) return;
        playerLoanMap.replace(player.getUniqueId(), loans);
    }
    public HashMap<UUID, List<Loan>> getPlayerLoanMap(){
        return this.getPlayerLoanMap();
    }
    public List<Loan> getPlayerLoans(Player player){
        return playerLoanMap.get(player.getUniqueId());
    }
    public List<Loan> getPlayerLoans(OfflinePlayer player){
        return playerLoanMap.get(player.getUniqueId());
    }
    public void addPlayerLoan(Loan loan, Player player){
        List<Loan> loanlist = playerLoanMap.get(player.getUniqueId());
        if (loanlist == null) loanlist = new ArrayList<Loan>();
        loanlist.add(loan);
        if (loanlist.size() == 1) playerLoanMap.put(player.getUniqueId(), loanlist);
        else playerLoanMap.replace(player.getUniqueId(), loanlist);
    }
    public void addPlayerLoan(Loan loan, OfflinePlayer player){
        List<Loan> loanlist = playerLoanMap.get(player.getUniqueId());
        if (loanlist == null) loanlist = new ArrayList<Loan>();
        loanlist.add(loan);
        if (loanlist.size() == 1) playerLoanMap.put(player.getUniqueId(), loanlist);
        else playerLoanMap.replace(player.getUniqueId(), loanlist);
    }
    public void removePlayerLoan(Loan loan, Player player){
        if (!playerLoanMap.containsKey(player.getUniqueId())) return;
        List<Loan> loanlist = playerLoanMap.get(player.getUniqueId());
        if (!loanlist.contains(loan)) return;
        loanlist.remove(loan);
        playerLoanMap.replace(player.getUniqueId(), loanlist);
    }
    public void removePlayerLoan(Loan loan, OfflinePlayer player){
        if (!playerLoanMap.containsKey(player.getUniqueId())) return;
        List<Loan> loanlist = playerLoanMap.get(player.getUniqueId());
        if (!loanlist.contains(loan)) return;
        loanlist.remove(loan);
        playerLoanMap.replace(player.getUniqueId(), loanlist);
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
        if(faction.getID() == 0) return;
        factionHashMap.remove(faction.getID());
        saveDB();
    }
    public void addPlayerToFaction(Faction faction, Player player, Rank rank){
        faction.addMember(player.getUniqueId(), rank);

        try{
            App.warThread.getWarFromFaction(faction).setPlayerBossBar(player);
        }
        catch (Exception e) {}
        faction.setInfluence(faction.getInfluence() + player_influence);
        if (playerHashMap.containsKey(player.getUniqueId())) playerHashMap.replace(player.getUniqueId(), faction.getID());
        else playerHashMap.put(player.getUniqueId(), faction.getID());

        //Messages
        for (Map.Entry mEntry : faction.getMembers().entrySet()){
            try{
                OfflinePlayer ofmember = Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
                if (!ofmember.isOnline()) continue;
                Player fmember = (Player) ofmember;
                fmember.playSound(fmember.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                fmember.sendMessage(zenfac + ChatColor.GREEN + player.getName() + " joined your faction!");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        player.sendMessage(zenfac + ChatColor.GREEN + "Joined faction: " + faction.getPrefix());
    }
    public void addPlayerToFaction(Faction faction, Player player){
        addPlayerToFaction(faction, player, faction.getDefaultRank());
    }
    
    public void removePlayerFromFaction(Faction faction, Player player){
        faction.removeMember(player.getUniqueId());
        try{
            App.warThread.getWarFromFaction(faction).removePlayerBossBar(player);
        }
        catch (Exception e) {}
        faction.setInfluence(faction.getInfluence() - player_influence);
        if (faction.getMembers().size() == 0) removeFaction(faction);
        playerHashMap.remove(player.getUniqueId());

        //Messages
        for (Map.Entry mEntry : faction.getMembers().entrySet()){
            OfflinePlayer ofmember = Bukkit.getOfflinePlayer((UUID) mEntry.getKey());
            if (!ofmember.isOnline()) continue;
            Player fmember = (Player) ofmember;
            fmember.playSound(fmember.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
            fmember.sendMessage(zenfac + ChatColor.RED + player.getName() + " left your faction!");
        }
    }
    public void changePlayerFaction(Faction newFaction, Player player, Rank rank){
        removePlayerFromFaction(getPlayerFaction(player), player);
        addPlayerToFaction(newFaction, player, rank);
        reloadScoreBoard(null);
    }
    public void changePlayerFaction(Faction newFaction, Player player){
        changePlayerFaction(newFaction, player, newFaction.getDefaultRank());
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

    @Nullable
    public String calcFQCName(int ChunkX, int ChunkZ, Byte offsetX, Byte offsetZ){
        int FQCX = ChunkX/100;
        int FQCZ = ChunkZ/100;
        if (ChunkX < 0) FQCX -= 1;
        if (ChunkZ < 0) FQCZ -= 1;
        if (offsetX != null) FQCX += (int) offsetX;
        if (offsetZ != null) FQCZ += (int) offsetZ;

        return "X" + String.valueOf(FQCX) + "Z" + String.valueOf(FQCZ);
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
        private Faction f;

        @Nullable
        public ClaimChunks(Player player_, Location location_, Faction faction_, Integer radius_, Thread waitThread){
            player = player_;
            if (location_ != null) location = location_;
            else location = player.getLocation();
            if (radius_ != null) radius = radius_;
            else radius = 1;

            if(radius > 12){
                if(player != null) player.sendMessage(zenfac + ChatColor.RED + "Maximum claim radius is 100!");
                return;
            }

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
            Faction playerFaction;

            if (f == null){
                if (player == null) return;
                playerFaction = getPlayerFaction(player);

                if (!playerFaction.getPlayerRank(player).hasPerm(claimchunkperm)){
                    App.getCommon().invalidRank(player, claimchunkperm);
                }
            }
            else playerFaction = f;

            //Deze lijst houdt bij welke FQC's we aangepast hebben!
            List<FactionQChunk> mFQCs = new ArrayList<FactionQChunk>();

            for (int i = -1*radius; i <= radius; i++){
                for (int j = -1*radius; j<= radius; j++){
                    int chunkX = chunk.getX();
                    int chunkZ = chunk.getZ();

                    //Check if points within circle
                    if (i*i + j*j >= radius*radius) continue;
                    chunkX += i;
                    chunkZ += j;

                    String FQCName = calcFQCName(chunkX, chunkZ, null, null);
                    FactionQChunk fqc = getFQC(FQCName);

                    Bukkit.getLogger().info("Chunk [" + String.valueOf(chunkX) + "," + String.valueOf(chunkZ) + "] in claimed in FQC: " +
                        FQCName + "@[" + String.valueOf(Math.abs(chunkX % 100)) + "," + String.valueOf(Math.abs(chunkZ % 100)) + "]");

                    byte[][] chunkData = fqc.getChunkData();
                    int ownerID = chunkData[Math.abs(chunkX % 100)][Math.abs(chunkZ % 100)];

                    //Check if the chunk is avaliable
                    if (ownerID != -1){
                        player.sendMessage(zenfac + ChatColor.DARK_RED + "This chunk is already claimed!");
                        continue;
                    }

                    //if it is, claim it!

                    //check if faction has enough influence to claim the chunk
                    //IF faction isn't specified
                    if (f == null){
                        double claimCost = plugin.getConfig().getDouble("Claim Influence Cost");
                        double oldBalance = playerFaction.getInfluence();
    
                        if (oldBalance < claimCost){
                            player.sendMessage(zenfac + ChatColor.DARK_RED + "Not enough faction influence to claim chunk!");
                            continue;
                        }

                        playerFaction.setInfluence(oldBalance - claimCost);
                    }

                    //this line does the actual claiming
                    chunkData[Math.abs(chunkX % 100)][Math.abs(chunkZ % 100)] = (byte) playerFaction.getID();

                    //record that we've changed a FQC
                    if (!mFQCs.contains(fqc)) mFQCs.add(fqc);

                    //update scoreboard
                    reloadScoreBoard(null);

                    if (player != null) player.sendMessage(zenfac + "Chunk Claimed!");
                    
                    //throw event for dynmap add-on
                    for (FactionQChunk changed : mFQCs){
                        AsyncFQCChangeEvent e = new AsyncFQCChangeEvent(changed, playerFaction);
                        e.callEvent();
                    }
                }
            }
        }
    }
    //Method om deze class te starten
    @Nullable
    public void claimChunks(Player player_, Location location_, Faction faction_, Integer radius_, Thread waitThread){
        new ClaimChunks(player_, location_, faction_, radius_, waitThread);
    }

    @Nullable
    public void reloadScoreBoard(Player player){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Objective objective;

        if (board.getObjective(DisplaySlot.SIDEBAR) != null) board.getObjective(DisplaySlot.SIDEBAR).unregister();
        
        objective = board.registerNewObjective("test", "dummy", ChatColor.BOLD + "Faction Influence");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Map.Entry mapElement : App.factionIOstuff.getFactionList().entrySet()){
            Faction value = (Faction) mapElement.getValue();
            Score score = objective.getScore(value.getPrefix());
            score.setScore( (int) value.getInfluence());
        }

        if (player != null) player.setScoreboard(board);
        else {
            for (Player p : Bukkit.getOnlinePlayers()){
                p.setScoreboard(board);
            }
        }
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
        
                String fQchunkName = calcFQCName(X, Z, offset[0], offset[1]);

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
        
                String fQchunkName = calcFQCName(X, Z, offset[0], offset[1]);

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
            try{
                t.join();
            }
            catch (InterruptedException i){
                Bukkit.getLogger().severe(zenfac + i.getLocalizedMessage());
            }
        }

        public void start(){
            if (t == null){
                t = new Thread(this);
                t.start();
            }
        }
        
        public void run(){
            plugin.getLogger().info("Loading databases...");
    
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
    
                    Colour defaultColour = new Colour(0x55FFFF, ChatColor.AQUA);
                    Faction defaultFaction = new Faction(default_fname, 0.0, 
                        null, 0, defaultColour);
                        
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

                    //say nice things in chat and parse legacy warps
                    //also register loans!
                    factionHashMap.values().forEach((faction) -> {
                        plugin.getLogger().info("Found Faction: " + faction.getPrefix());
                        parseLegacy(faction);

                        //now register the loans!
                        faction.getRunningLoans().forEach((loan) -> {
                            addPlayerLoan(loan, loan.getOfflinePlayer());
                        });
                    });

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

        private void parseLegacy(Faction faction){
            if(faction.hasLegacyWarps()){
                Bukkit.getLogger().info(App.zenfac + "Found legacy (pre 0.1.15) warps! Parsing data...");

                //parse all the legacy warps
                faction.getLegacyWarps().forEach((warp) ->{
                    //add permission to warp
                    warp.setPerm();

                    //add permission to the right rank!
                    int rankReq = warp.getOldRankReq();

                    if(rankReq >= 0) faction.getRanks().get(0).addPerm(warp.getPerm());
                    if(rankReq >= 1) faction.getRanks().get(1).addPerm(warp.getPerm());
                    if(rankReq >= 2) faction.getRanks().get(2).addPerm(warp.getPerm());
                });

                //delete the legacy warps we no longer need!
                faction.clearLegacyWarps();
            }
            Bukkit.getLogger().info(App.zenfac + ChatColor.DARK_GRAY + "Successfully converted warp!");
        }
    }

}