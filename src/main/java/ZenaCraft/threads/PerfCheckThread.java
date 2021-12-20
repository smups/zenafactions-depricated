package ZenaCraft.threads;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;

public class PerfCheckThread {

    private HashMap<String, List<Long>> commandPerf = new HashMap<String,List<Long>>();

    private Plugin plugin = App.getPlugin(App.class);

    private String[] folders = new String[] {"plugins/ZenaFactions/logs/", "commands"};

    private Timer t;

    public PerfCheckThread(){

        App.logging = true;

        //make dirs if they don't exits!
        plugin.getLogger().info("Starting logging db init...");

        for (String folder_name : folders){
            if(!folder_name.equals(folders[0]))
                folder_name = folders[0] + folder_name;

            File dat = new File(folder_name);

            if(!dat.exists()){
                dat.mkdirs();
                plugin.getLogger().info(App.zenfac + "No "+ folder_name +" folder found. Making one...");
            }
        }

        t = new Timer();
        t.schedule(new SaveDat(),1000*(3600 + 5), 1000*3600);

    }

    //loggers
    public void logcommand(String command, long time){
        List<Long> entries = commandPerf.get(command);
        if (entries == null) entries = new ArrayList<Long>();
        entries.add(time);
        commandPerf.put(command, entries);
    }

    private class SaveDat extends TimerTask{
        @Override
        public void run() {
            save();
        }
    }

    private void save(){
        Bukkit.getLogger().info(App.zenfac + ChatColor.DARK_GRAY + "writing log data...");

        try{
            for(String folder_name : folders){
                if(folder_name.equals(folders[0])) continue;

                folder_name = folders[0] + folder_name;
                String file_name = folder_name + "/" +
                    LocalDateTime.now().toString().replaceAll(":", "-").split("\\.")[0] +
                    ".json";

                File dat = new File(file_name);

                if(commandPerf.isEmpty()) continue;

                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(dat, commandPerf);
            }

            Bukkit.getLogger().info(App.zenfac + "saved log data!");
        }
        catch (IOException e){
            Bukkit.getLogger().severe(e.getMessage());
        }

        commandPerf.clear();
    }

    public void disable(){
        t.cancel();
        save();
    }

    public void interrupt(){
        t.cancel();
        save();
        t = new Timer();
        t.schedule(new SaveDat(), 1000*3600);
    }
}