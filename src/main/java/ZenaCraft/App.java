package ZenaCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ZenaCraft.commands.createFaction;
import ZenaCraft.commands.factionBalance;
import ZenaCraft.commands.factionInfluence;
import ZenaCraft.commands.listFactions;
import ZenaCraft.commands.saveDB;
import ZenaCraft.events.PlayerJoin;
import ZenaCraft.objects.Faction;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


public final class App extends JavaPlugin
{
    PluginManager pm = getServer().getPluginManager();
    Logger log = Bukkit.getLogger();

    public static String zenfac = ChatColor.AQUA + "[ZenaFactions] ";
    public static String player_db = "plugins/ZenaFactions/dat/players.ser";
    public static String faction_db = "plugins/ZenaFactions/dat/factions.ser";
    public static HashMap<String, Faction> factionHashMap = new HashMap<String, Faction>();
    public static HashMap<UUID, String> playerHashMap = new HashMap<UUID, String>();

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    @Override
    public void onEnable(){
        //say something to console
        getLogger().info(zenfac + "Loading ZenaFactions...");

        //load config
        saveDefaultConfig();

        //intiate db
        initDB();

        //Hook into Vault
        if (!setupEconomy()){
            getLogger().severe("Plugin disabled, no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.setupPermissions();
        //this.setupChat();
        getLogger().info("Hooked into Vault!");

        //Events
        pm.registerEvents(new PlayerJoin(), this);

        //Commands
        getCommand("listFactions").setExecutor(new listFactions());
        getCommand("createFaction").setExecutor(new createFaction());
        getCommand("saveDB").setExecutor(new saveDB());
        getCommand("factionBalance").setExecutor(new factionBalance());
        getCommand("factionInfluence").setExecutor(new factionInfluence());
    }

    @Override
    public void onDisable(){
        //save factiondb
        saveDB();
        
        getLogger().info(zenfac + "Byeee");
    }

    private void initDB(){
        getLogger().info("Starting database init...");

        File dat = new File("plugins/ZenaFactions/dat");

        if(!dat.exists()){
            dat.mkdirs();
            getLogger().info(zenfac + "No dat folder found. Making one...");
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
                getLogger().info(zenfac + "Created player data");
            }
            catch (IOException i){
                i.printStackTrace();
            }
        }
        else{
            try{
                getLogger().info(zenfac + "Loading player data...");
                FileInputStream file = new FileInputStream(player_db);
                ObjectInputStream in = new ObjectInputStream(file);
                playerHashMap = (HashMap<UUID, String>) in.readObject();
                in.close();
                file.close();

                for (Map.Entry mapElement : factionHashMap.entrySet()){
                    String value = (String) mapElement.getValue();
                    getLogger().info("Found Player: " + value);
                }
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
                HashMap<String, Faction> player_factions = new HashMap<String, Faction>();

                HashMap<UUID, Integer> dummyHashMap = new HashMap<UUID, Integer>();
                String[] defaultRanks = {"Admin", "Staff", "New Player"};
                Faction defaultFaction = new Faction("default", defaultRanks, 0.0, dummyHashMap, "default");

                player_factions.put("default", defaultFaction);
                factionHashMap.put("default", defaultFaction);

                out.writeObject(player_factions);
                out.close();
                file.close();
                getLogger().info(zenfac + "Created faction data");
            }
            catch (IOException i){
                i.printStackTrace();
            }
            
        }
        else{
            try{
                getLogger().info(zenfac + "Loading faction data...");
                FileInputStream file = new FileInputStream(faction_db);
                ObjectInputStream in = new ObjectInputStream(file);
                factionHashMap = (HashMap<String, Faction>) in.readObject();
                in.close();
                file.close();

                for (Map.Entry mapElement : factionHashMap.entrySet()){
                    String key = (String) mapElement.getKey();
                    getLogger().info("Found Faction: " + key);
                }
            }
            catch (IOException i){
                i.printStackTrace();
            }
            catch(ClassNotFoundException c){
                c.printStackTrace();
            }
        }        
        getLogger().info(zenfac + "Database init finished!");
    }

    private void saveDB(){
        try{
            FileOutputStream file = new FileOutputStream(faction_db);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(factionHashMap);
            out.close();
            file.close();
            getLogger().info(zenfac + "Saved faction data");
        }
        catch (IOException i){
            i.printStackTrace();
        }

        try{
            FileOutputStream file = new FileOutputStream(player_db);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(playerHashMap);
            out.close();
            file.close();
            getLogger().info(zenfac + "Saved player data");
        }
        catch (IOException i){
            i.printStackTrace();
        }
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
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
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
}