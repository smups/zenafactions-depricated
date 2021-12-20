package ZenaCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import ZenaCraft.commands.faction.*;
import ZenaCraft.commands.claims.*;
import ZenaCraft.commands.financial.*;
import ZenaCraft.commands.warps.*;
import ZenaCraft.commands.wars.*;
import ZenaCraft.commands.ranks.*;
import ZenaCraft.commands.*;

import ZenaCraft.listeners.*;
import ZenaCraft.objects.Rank;
import ZenaCraft.threads.*;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


public final class App extends JavaPlugin
{
    PluginManager pm = getServer().getPluginManager();
    Logger log = Bukkit.getLogger();

    public static String zenfac = ChatColor.AQUA + "[ZenaFactions] ";
    public static String player_db = "plugins/ZenaFactions/dat/players.ser";
    public static String faction_db = "plugins/ZenaFactions/dat/factions.ser";
    public static String FQChunk_db = "plugins/ZenaFactions/dat/";
    public static String war_db = "plugins/ZenaFactions/dat/wars.ser";

    //perms
    public static List<String> permList = new ArrayList<String>();
    public static List<Rank> defranks = new ArrayList<Rank>();
    public static List<String> lvl1_perms = new ArrayList<String>();
    public static List<String> lvl2_perms = new ArrayList<String>();
    public static List<String> lvl3_perms = new ArrayList<String>();

    private static Common common;

    public static FactionIOstuff factionIOstuff;
    public static WarThread warThread;
    public static PerfCheckThread perfThread;

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    public static boolean EU = false;
    public static boolean logging = false;

    private Timer t;

    String default_fname = getConfig().getString("default faction name");
    String currentVersionTitle;
    double currentVersion;

    @Override
    public void onEnable() {
        // say something to console
        getLogger().info(zenfac + "Loading ZenaFactions...");

        currentVersionTitle = getDescription().getVersion().trim();
        currentVersion = Double.valueOf(currentVersionTitle.replaceAll("\\.", ""));

        common = new Common();

        //check if we are in the EU and have to display a GDPR message
        if(ZonedDateTime.now().getZone().equals(ZoneId.of("Europe/Amsterdam"))) EU = true;

        // load config
        saveDefaultConfig();

        //start performance loggin
        if(getConfig().getBoolean("Performance logging"))
            perfThread = new PerfCheckThread();

        // Hook into Vault
        if (!setupEconomy()) {
            getLogger().severe(zenfac + "Plugin disabled, no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.setupPermissions();
        // this.setupChat();
        getLogger().info(zenfac + "Hooked into Vault!");

        // Hook into GP
        if (!setupGP()) {
            getLogger().severe(zenfac + "Plugin disabled, no GriefPrevention dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Events
        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerLeave(), this);
        pm.registerEvents(new PlayerMove(), this);
        pm.registerEvents(new PlayerChat(), this);
        pm.registerEvents(new CreateClaim(), this);
        pm.registerEvents(new mobDeath(), this);
        pm.registerEvents(new PlayerDeath(), this);
        pm.registerEvents(new ModifyClaim(), this);
        // pm.registerEvents(new PlayerDamage(), this); pvp damage, very annyoing
        pm.registerEvents(new PlayerTeleport(), this);
        pm.registerEvents(new Respawn(), this);
        if (getConfig().getBoolean("bindBeaconToFaction"))
            pm.registerEvents(new BeaconEffect(), this);

        // Commands
        getCommand("listFactions").setExecutor(new ListFactions());
        getCommand("createFaction").setExecutor(new CreateFaction());
        getCommand("saveDB").setExecutor(new SaveDB());
        getCommand("factionBalance").setExecutor(new FactionBalance());
        getCommand("factionInfluence").setExecutor(new FactionInfluence());
        // getCommand("setPrefix").setExecutor(new setPrefix()); -- depricated
        getCommand("listLoadedFQChunks").setExecutor(new ListLoadedFQCs());
        getCommand("claimChunk").setExecutor(new ClaimChunk());
        getCommand("toggleAutoClaim").setExecutor(new ToggleAutoClaim());
        getCommand("listMembers").setExecutor(new ListMembers());
        getCommand("setInfluence").setExecutor(new SetInfluence());
        getCommand("joinFaction").setExecutor(new JoinFaction());
        //getCommand("promote").setExecutor(new Promote()); -- depricated
        //getCommand("demote").setExecutor(new Demote()); -- depricated
        //getCommand("changeRankNames").setExecutor(new SetRankNames()); -- depricated
        getCommand("declarewar").setExecutor(new DeclareWar());
        getCommand("warscore").setExecutor(new WarScore());
        getCommand("listwars").setExecutor(new ListWars());
        getCommand("addtowarzone").setExecutor(new AddToWarZone());
        getCommand("createWarp").setExecutor(new CreateWarp());
        getCommand("warp").setExecutor(new WarpPlayer());
        getCommand("listwarps").setExecutor(new ListWarps());
        getCommand("changefactioncolour").setExecutor(new ChangeColour());
        getCommand("changefactionname").setExecutor(new ChangeFactionName());
        getCommand("setwarptax").setExecutor(new SetWarpTax());
        //getCommand("setwarprank").setExecutor(new SetWarpRequirement()); -- depricated
        getCommand("createloan").setExecutor(new CreateLoan());
        getCommand("listloans").setExecutor(new ListLoans());
        getCommand("takeloan").setExecutor(new TakeLoan());
        getCommand("payloan").setExecutor(new PayLoan());
        getCommand("depositfaction").setExecutor(new DepositFaction());
        getCommand("withdrawfaction").setExecutor(new WithdrawFaction());
        getCommand("myloans").setExecutor(new MyLoans());
        getCommand("buyfactionbanner").setExecutor(new BuyFactionBanner());
        getCommand("changefactionbanner").setExecutor(new ChangeFactionBanner());
        getCommand("lognow").setExecutor(new LogNow());
        getCommand("createrank").setExecutor(new CreateRank());
        getCommand("removerank").setExecutor(new RemoveRank());
        getCommand("changerank").setExecutor(new ChangeRank());
        getCommand("makewarpprivate").setExecutor(new MakeWarpPrivate());
        getCommand("allowwarp").setExecutor(new AllowWarp());
        getCommand("listperms").setExecutor(new ListPerms());
        getCommand("addrankperm").setExecutor(new AddRankPerm());
        getCommand("removerankperm").setExecutor(new RemoveRankPerm());

        //all the ranks are now loaded, so we can create the default ranks:
        Rank lvl1 = new Rank(getConfig().getString("Default Rank name lvl1"));
        lvl1.setPerms(lvl1_perms);
        defranks.add(lvl1);
        Rank lvl2 = new Rank(getConfig().getString("Default Rank name lvl2"));
        lvl2.setPerms(lvl2_perms);
        defranks.add(lvl2);
        Rank lvl3 = new Rank(getConfig().getString("Default Rank name lvl3"));
        lvl3.setPerms(lvl3_perms);
        defranks.add(lvl3);

        // intiate db
        factionIOstuff = new FactionIOstuff(player_db, faction_db, zenfac, FQChunk_db);

        // all the way at the end we setup the war database
        warThread = new WarThread(war_db);

        //and setup the version checker
        long hourint = getConfig().getLong("Version check interval");
        t = new Timer();
        t.schedule(new TikTok(), 0, hourint*1000*3600);
    }

    @Override
    public void onDisable() {
        // save factiondb
        factionIOstuff.saveDB();

        // save wardb
        warThread.saveDB();

        //stop updatechecker
        t.cancel();

        //stop perflogger
        perfThread.disable();
        
        //byeeee
        getLogger().info(zenfac + "Byeee");
    }

    private class TikTok extends TimerTask{
        @Override
        public void run() {
            log.info(App.zenfac + "Looking for a new version...");

            String newVersionTitle = getNewestVersion();
            if (newVersionTitle == null) return;

            double newVersion = Double.valueOf(newVersionTitle.replaceAll("\\.", ""));

            String msg;

            if (newVersion > currentVersion){
               msg = "New version: v" + newVersionTitle + " found! (Current version: v" + currentVersionTitle + 
                ") Please update your ZenaFactions Install! You can download the new version from: " +
                "https://dev.bukkit.org/projects/zenafactions/files/latest";
                log.warning(App.zenfac + msg);
            }
            else{
                msg = ChatColor.DARK_GRAY + "Newest version: v" + newVersionTitle + ", current version: " + currentVersionTitle + ", ZenaFactions is up to date!";
                log.info(App.zenfac + msg);
            }

            for(OfflinePlayer op : Bukkit.getOperators()){
                if (!op.isOnline()) continue;
                Player p = (Player) op.getPlayer();
                p.sendMessage(App.zenfac + ChatColor.DARK_GRAY + msg);
            }
        }
    }

    private boolean setupEconomy() {
        // Two things can go wrong here

        // no Vault installed
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        // No economy plugin installed
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        // Okay so everything is fine, now pass on the economy
        econ = rsp.getProvider();
        return econ != null;

    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (perms == null) {
            getLogger().severe("No Chatprovider found, please install one");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        return perms != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    public static Common getCommon(){
        return common;
    }

    private boolean setupGP() {
        // no GP installed
        if (Bukkit.getPluginManager().getPlugin("GriefPrevention") == null)
            return false;
        return true;
    }

    //permission list
    public static void registerPerm(String perm, int def){
        if (def == 0) lvl1_perms.add(perm);
        else if (def == 1) lvl2_perms.add(perm);
        else if (def == 2) lvl3_perms.add(perm);
        permList.add(perm);
    }

    private String getNewestVersion(){
        try{
            URL url = new URL("https://servermods.forgesvc.net/servermods/files?projectids=441588");

            final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            final String response = reader.readLine();

            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() == 0){
                log.warning("Updatefile is empty!");;
                return null;
            }

            // Pull the last version from the JSON
            String newVersionTitle = ((String) ((JSONObject) array.get(array.size() - 1)).get("fileName")).split("-")[1].trim();
            return newVersionTitle;
        }
        catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }
}