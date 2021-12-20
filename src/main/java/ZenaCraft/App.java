package ZenaCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import ZenaCraft.commands.*;

import ZenaCraft.listeners.*;
import ZenaCraft.threads.*;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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

    private static Common common;

    public static FactionIOstuff factionIOstuff;
    public static WarThread warThread;

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    String default_fname = getConfig().getString("default faction name");

    @Override
    public void onEnable() {
        // say something to console
        getLogger().info(zenfac + "Loading ZenaFactions...");

        String currentVersionTitle = getDescription().getVersion();
        double currentVersion = Double.valueOf(currentVersionTitle.replaceAll("\\.", ""));

        common = new Common();

        // load config
        saveDefaultConfig();

        // intiate db
        factionIOstuff = new FactionIOstuff(player_db, faction_db, zenfac, FQChunk_db);

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
        getCommand("promote").setExecutor(new Promote());
        getCommand("demote").setExecutor(new Demote());
        getCommand("changeRankNames").setExecutor(new SetRankNames());
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
        getCommand("setwarprank").setExecutor(new SetWarpRequirement());
        getCommand("createloan").setExecutor(new CreateLoan());
        getCommand("listloans").setExecutor(new ListLoans());
        getCommand("takeloan").setExecutor(new TakeLoan());
        getCommand("payloan").setExecutor(new PayLoan());
        getCommand("depositfaction").setExecutor(new DepositFaction());
        getCommand("withdrawfaction").setExecutor(new WithdrawFaction());
        getCommand("myloans").setExecutor(new MyLoans());
        getCommand("buyfactionbanner").setExecutor(new BuyFactionBanner());
        getCommand("changefactionbanner").setExecutor(new ChangeFactionBanner());

        // all the way at the end we setup the war database
        warThread = new WarThread(war_db);

        //and we do a little version check
        updateCheck(currentVersion);
    }

    @Override
    public void onDisable() {
        // save factiondb
        factionIOstuff.saveDB();

        // save wardb
        warThread.saveDB();

        getLogger().info(zenfac + "Byeee");
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

    private double updateCheck(double currentVersion){
        try{
            URL url = new URL("https://api.curseforge.com/servermods/files?projectids=441588");
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("User-Agent", "ZenaFactions Update Checker");
            conn.setDoOutput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() == 0){
                log.warning("Could not find update file!");;
            }

            // Pull the last version from the JSON
            String newVersionTitle = ((String) ((JSONObject) array.get(array.size() - 1)).get("name")).replace("ZenaFactions", "").trim();
            return Double.valueOf(newVersionTitle.replaceAll("\\.", "").trim());
        }
        catch (Exception e) {
            log.info("There was an issue attempting to check for the latest version.");
        }
        return currentVersion;
    }
}