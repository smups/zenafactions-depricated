package ZenaCraft.commands.wars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.War;

public class ListWars extends TemplateCommand{

    public ListWars() {super(0);}

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
        String reply = App.zenfac + "Ongoing wars:\n";
        for (War war : App.warThread.getWars()){
            reply += (war.getAttackers().getPrefix() + ChatColor.RESET + " VS " + war.getDefenders().getPrefix() + ",\n");
        }

        if (player != null) player.sendMessage(reply);
        if (log != null) log.info(reply);
        
        return true;
    }
}