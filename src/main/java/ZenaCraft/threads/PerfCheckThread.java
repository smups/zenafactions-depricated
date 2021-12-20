package ZenaCraft.threads;

import java.io.File;
import java.io.FileInputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.objects.PerfReport;

public class PerfCheckThread {

    private HashMap<String, List<Long>> commandPerf = new HashMap<String,List<Long>>();

    private Plugin plugin = App.getPlugin(App.class);

    private String[] folders = new String[] {"plugins/ZenaFactions/logs/"};

    //Timers
    private Timer saveTimer;
    private Timer sendTimer;

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
            }
        }

        saveTimer = new Timer("saveTimer");
        saveTimer.schedule(new SaveDat(), 1000*(3600 + 5), 1000*3600);
        
        sendTimer = new Timer("sendTimer");

        //Pick an offset anywhere from now to 1h, don't want people to play dirty :c
        int offset = (int) (Math.random()*3600);
        sendTimer.schedule(new SendDat(), 1000*(10 + offset), 3*1000*3600);
    }

    //loggers
    public void logcommand(String command, long time){
        List<Long> entries = commandPerf.get(command);
        if (entries == null) entries = new ArrayList<Long>();
        entries.add(time);
        commandPerf.put(command, entries);
    }

    //Save Task
    private class SaveDat extends TimerTask{
        @Override
        public void run() {
            PerfReport report = createReport();
            if (report == null) return;
            report.save();
            commandPerf.clear();
        }
    }

    //Send Task
    private class SendDat extends TimerTask{
        Logger log = Bukkit.getLogger();

        @Override
        public void run() {
            // No. of files sent
            int sent = 0;

            try{
                File folder = new File("plugins/ZenaFactions/logs");               

                for (File file : listFileTree(folder)){

                    //Ignore files larger than 10MB
                    long MB = 10000000;
                    if (file.length() > MB){
                        Bukkit.getLogger().warning(App.zenfac +
                        "Found a log file larger than 10MB. Please" +
                        " decrease your logging interval to decrease" +
                        " the size of the logfiles!");
                        continue;
                    }

                    //get data
                    FileInputStream in = new FileInputStream(file);

                    //send data
                    boolean response = sendData(in);

                    in.close();

                    //delete data
                    if (response) file.delete();

                    sent++;
                }
            }
            catch (Exception e){
                log.warning(App.zenfac + "IOError: " + e.getMessage());
            }

            Bukkit.getLogger().info(App.zenfac + "Sent " + String.valueOf(sent) +
                " logfiles to the dev! Thanks for participating!"); 
        }
    }

    private PerfReport createReport(){
        if (commandPerf.isEmpty()) return null;
        return new PerfReport(commandPerf);
    }

    private Collection<File> listFileTree(File dir) {
        Set<File> fileTree = new HashSet<File>();
        if(dir==null||dir.listFiles()==null){
            return fileTree;
        }
        for (File entry : dir.listFiles()) {
            if (entry.isFile()) fileTree.add(entry);
            else fileTree.addAll(listFileTree(entry));
        }
        return fileTree;
    }

    private boolean sendData(FileInputStream dataStream){
        Logger log = Bukkit.getLogger();

        try{
            URL url = new URL("http://zenafactions.tk:9999/PerfLogServer");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            log.info(App.zenfac + "Connecting to performance logging server...");

            //config the request
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            //conn.setRequestProperty("Content-Type", "application/json; utf-8");
            //conn.setRequestProperty("Accept", "application/json");

            //set output
            conn.setDoOutput(true);

            //get datta
            byte[] data = dataStream.readAllBytes();
        
            //get outputstream and write stream data
            conn.connect();
            conn.getOutputStream().write(data);

            String response = null;
            response = conn.getResponseMessage();

            log.info(App.zenfac + "Sent logfile! Server replied: " + response);

            return true;
        }
        catch (HttpConnectTimeoutException httpError){
            log.warning(App.zenfac + "The logging server is overloaded! Better luck in 6h :c ");
            return false;
        }
        catch (HttpTimeoutException timeout){
            log.warning(App.zenfac + "The logging server timed out! It's probaby offline, or very busy! :c ");
            return false;
        }
        catch (ConnectException connectError){
            log.warning(App.zenfac + "Connection error: " + connectError.getMessage() + " Caused by: " + connectError.getCause());
            return false;
        }
        catch (Exception e){
            Bukkit.getLogger().info(App.zenfac + "PerfLog Error: " + e.getMessage());
            return false;
        }

    }

    public void disable(){
        saveTimer.cancel();
        if (!commandPerf.isEmpty()) createReport().save();
        sendTimer.cancel();
    }

    public void interrupt(){
        saveTimer.cancel();
        PerfReport report = createReport();
        if (report != null) report.save();
        commandPerf.clear();
        saveTimer = new Timer();
        saveTimer.schedule(new SaveDat(), 1000*(3600 + 5), 1000*3600);
    }
}