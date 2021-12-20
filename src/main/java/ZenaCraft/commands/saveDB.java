package ZenaCraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import net.md_5.bungee.api.ChatColor;

public class SaveDB extends TemplateCommand{

    public SaveDB() {super(0);}

    @Override
    protected boolean getSender(CommandSender sender) {
        if(sender instanceof Player){
            player = (Player) sender;
            return true;
        }
        else{
            log = Bukkit.getLogger();
            return true;
        }
    }

    @Override
    protected boolean run() {
        if (player != null){
            if (!player.isOp()){
                player.sendMessage(App.zenfac + ChatColor.RED + "You are not permitted to use this command");
                return true;
            }
        }

        if (log != null) log.info(App.zenfac + "saving...");
        App.factionIOstuff.saveDB();;
        return true;
    }
}