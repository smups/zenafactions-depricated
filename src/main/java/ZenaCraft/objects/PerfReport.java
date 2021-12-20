package ZenaCraft.objects;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import oshi.SystemInfo;

public class PerfReport{

    //Report info
    final private String logTime;

    //System info
    final private String[] CPU;
    final private String Memory;
    final private String OS;
    
    //JVM info
    final private int processors;
    final private long totalMem;
    final private long currentMem;
    final private String JVMVersion;

    //Server info
    final private String serverType;
    final private String MCVersion;
    final private int knownPlayers;
    final private int onlinePlayers;
    final private boolean online;

    //ZF info
    final private String ZFVersion;
    final private String ZFDynmapVersion;
    final private String ZFDiscordVersion;
    final private int numFac;
    final private int numLoans;
    final private int numWars;

    //performance metrics
    private final HashMap<String, List<Long>> commandPerformance;

    //Location of log reports
    private transient String loc = "plugins/ZenaFactions/logs";

    //transient stuff
    private transient Plugin plugin = App.getPlugin(App.class);
    
    public PerfReport(HashMap<String, List<Long>> commandPerformance){
        //set date
        logTime = LocalDateTime.now().toString();

        //get System info
        SystemInfo sysinfo = new SystemInfo();
        CPU = sysinfo.getHardware().getProcessor().toString().split("\n");
        OS = sysinfo.getOperatingSystem().toString();
        Memory = sysinfo.getHardware().getMemory().toString();

        //get JVM information
        Runtime runTime = Runtime.getRuntime();
        processors = runTime.availableProcessors();
        totalMem = runTime.totalMemory();
        currentMem = totalMem - runTime.freeMemory();
        JVMVersion = runTime.version().toString();
        
        //get Server information
        Server server = Bukkit.getServer();
        serverType = server.getName();
        MCVersion = server.getMinecraftVersion();
        knownPlayers = server.getOfflinePlayers().length;
        onlinePlayers = server.getOnlinePlayers().size();
        online = server.getOnlineMode();

        //get ZF info
        ZFVersion = App.getPlugin(App.class).getDescription().getVersion().trim();
        Plugin ZFDynmap = Bukkit.getPluginManager().getPlugin("ZFDynmap");
        if (ZFDynmap == null) ZFDynmapVersion = "not installed";
        else ZFDynmapVersion = ZFDynmap.getDescription().getVersion().trim();
        Plugin ZFDiscord = Bukkit.getPluginManager().getPlugin("ZFDiscord");
        if (ZFDiscord == null) ZFDiscordVersion = "not installed";
        else ZFDiscordVersion = ZFDiscord.getDescription().getVersion().trim();
        numFac = App.factionIOstuff.getFactionList().size();
        numLoans = App.factionIOstuff.calcNumLoans();
        numWars = App.warThread.getWars().size();

        //set performance
        this.commandPerformance = commandPerformance;
    }

    public void save(){
        try{
            String fileName = loc + "/" + logTime.replaceAll(":", "-").split("\\.")[0] + ".json";
            File file = new File(fileName);

            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            mapper.writeValue(file, this);
            plugin.getLogger().info(App.zenfac + ChatColor.GRAY + "Succesfully created log file!");
        }
        catch (Exception e){
            plugin.getLogger().warning(App.zenfac + "Something went wrong while logging. Error msg: " +
                e.getMessage() + " Caused by: " + e.getCause());
        }
    }
}