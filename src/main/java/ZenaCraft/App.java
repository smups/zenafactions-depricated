package ZenaCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ZenaCraft.commands.*;
import ZenaCraft.listeners.*;
import ZenaCraft.objects.Faction;
import ZenaCraft.threads.*;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

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

    public static FactionIOstuff factionIOstuff;
    public static WarThread warThread;

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    String default_fname = getConfig().getString("default faction name");

    @Override
    public void onEnable(){
        //say something to console
        getLogger().info(zenfac + "Loading ZenaFactions...");

        //load config
        saveDefaultConfig();

        //intiate db
        factionIOstuff = new FactionIOstuff(player_db, faction_db, zenfac, FQChunk_db);

        //Hook into Vault
        if (!setupEconomy()){
            getLogger().severe("Plugin disabled, no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.setupPermissions();
        //this.setupChat();
        getLogger().info("Hooked into Vault!");

        //Hook into GP
        if (!setupGP()){
            getLogger().severe("Plugin disabled, no GriefPrevention dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return; 
        }

        //Events
        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerLeave(), this);
        pm.registerEvents(new PlayerMove(), this);
        pm.registerEvents(new PlayerChat(), this);
        pm.registerEvents(new CreateClaim(), this);
        pm.registerEvents(new mobDeath(), this);
        pm.registerEvents(new PlayerDeath(), this);
        pm.registerEvents(new ModifyClaim(), this);
        //pm.registerEvents(new PlayerDamage(), this); pvp damage, very annyoing
        pm.registerEvents(new PlayerTeleport(), this);
        pm.registerEvents(new Respawn(), this);
        if (getConfig().getBoolean("bindBeaconToFaction")) pm.registerEvents(new BeaconEffect(), this);

        //Commands
        getCommand("listFactions").setExecutor(new listFactions());
        getCommand("createFaction").setExecutor(new createFaction());
        getCommand("saveDB").setExecutor(new saveDB());
        getCommand("factionBalance").setExecutor(new factionBalance());
        getCommand("factionInfluence").setExecutor(new factionInfluence());
        //getCommand("setPrefix").setExecutor(new setPrefix()); -- depricated
        getCommand("listLoadedFQChunks").setExecutor(new ListLoadedFQCs());
        getCommand("claimChunk").setExecutor(new claimChunk());
        getCommand("toggleAutoClaim").setExecutor(new toggleAutoClaim());
        getCommand("listMembers").setExecutor(new listMembers());
        getCommand("setInfluence").setExecutor(new setInfluence());
        getCommand("joinFaction").setExecutor(new joinFaction());
        getCommand("promote").setExecutor(new promote());
        getCommand("demote").setExecutor(new demote());
        getCommand("changeRankNames").setExecutor(new setRankNames());
        getCommand("declarewar").setExecutor(new declarewar());
        getCommand("warscore").setExecutor(new warscore());
        getCommand("listwars").setExecutor(new listwars());
        getCommand("addtowarzone").setExecutor(new AddToWarZone());
        getCommand("createWarp").setExecutor(new createWarp());
        getCommand("warp").setExecutor(new warp());
        getCommand("listwarps").setExecutor(new ListWarps());
        getCommand("changefactioncolour").setExecutor(new ChangeColour());
        getCommand("changefactionname").setExecutor(new ChangeFactionName());
        getCommand("setwarptax").setExecutor(new SetWarpTax());
        getCommand("setwarprank").setExecutor(new SetWarpRequirement());
        getCommand("createloan").setExecutor(new CreateLoan());
        getCommand("listloans").setExecutor(new ListLoans());
        getCommand("takeloan").setExecutor(new TakeLoan());
        getCommand("payloan").setExecutor(new PayLoan());

        //all the way at the end we setup the war database
        warThread = new WarThread(war_db);
    }

    @Override
    public void onDisable(){
        //save factiondb
        factionIOstuff.saveDB();

        //save wardb
        warThread.saveDB();
        
        getLogger().info(zenfac + "Byeee");
    }

    private boolean setupEconomy(){
        //Two things can go wrong here

        //no Vault installed
        if (Bukkit.getPluginManager().getPlugin("Vault") == null){
            return false;
        }

        //No economy plugin installed
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null){
            return false;
        }

        //Okay so everything is fine, now pass on the economy
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

    private boolean setupGP(){
        //no GP installed
        if (Bukkit.getPluginManager().getPlugin("GriefPrevention") == null) return false;
        return true;
    }

    //Messages
    public static boolean invalidSyntax(Player player){
        player.sendMessage(zenfac + ChatColor.RED + "Invalid Syntax! Use /help zenafactions for help");
        return true;
    }
    public static boolean invalidRank(Player player, int rankReq){
        Faction f = factionIOstuff.getPlayerFaction(player);
        player.sendMessage(zenfac + ChatColor.RED + "You don't have the appropriate rank to do this!" +
            " You have to be at least: " + f.getColour().asString() + f.getRanks()[rankReq]);
        return true;
    }
}