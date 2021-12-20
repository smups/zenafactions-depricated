package ZenaCraft.commands;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import net.md_5.bungee.api.ChatColor;

public class saveDB implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (!player.isOp()){
                player.sendMessage(App.zenfac + ChatColor.DARK_RED + "You are not permitted to use this command");
                return true;
            }
        }
        sender.sendMessage(App.zenfac + "Saving databases");
        Saving obj = new Saving();
        Thread tr = new Thread(obj);
        tr.start();
        return true;
    }

    private class Saving implements Runnable{

        public void run(){
            try{
                FileOutputStream file = new FileOutputStream(App.faction_db);
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(App.factionHashMap);
                out.close();
                file.close();
            }
            catch (IOException i){
                i.printStackTrace();
            }
    
            try{
                FileOutputStream file = new FileOutputStream(App.player_db);
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(App.playerHashMap);
                out.close();
                file.close();
            }
            catch (IOException i){
                i.printStackTrace();
            }
        }
    }
}