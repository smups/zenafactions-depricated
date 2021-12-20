package ZenaCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import ZenaCraft.events.PlayerJoin;
//import ZenaCraft.objects.Faction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;


public final class App extends JavaPlugin
{
    PluginManager pm = getServer().getPluginManager();
    Logger log = Bukkit.getLogger();
    //Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

    public static String zenfac = ChatColor.AQUA + "[ZenaFactions] ";
    public static String player_db = "plugins/ZenaFactions/dat/players.ser";
    public static String faction_db = "plugins/ZenaFactions/dat/factions.ser";

    @Override
    public void onEnable(){
        //load config
        //saveDefaultConfig();

        //intiate db
        initDB();

        //intiate scoreboard

        //say something to console
        getLogger().info("Loading ZenaFactions...");

        //Events
        pm.registerEvents(new PlayerJoin(), this);

        //Commands
    }

    @Override
    public void onDisable(){
        getLogger().info("Byeee");
    }

    private void initDB(){
        getLogger().info("Starting database init...");

        File dat = new File("plugins/ZenaFactions/dat");

        if(!dat.exists()){
            dat.mkdirs();
            getLogger().info("No dat folder found. Making one...");
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
                getLogger().info("Created player data");
            }
            catch (IOException i){
                i.printStackTrace();
            }
        }
        /*
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
                out.writeObject(player_factions);
                out.close();
                file.close();
                getLogger().info("Created faction data");
            }
            catch (IOException i){
                i.printStackTrace();
            }
            
        }
        */
        getLogger().info("Database init finished!");
    }
}