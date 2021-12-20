package ZenaCraft.commands.faction;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class ListFactions extends TemplateCommand{

    public ListFactions() {super(0);}

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
        String response = App.zenfac + "Current factions: ";

        for (Map.Entry mapElement : App.factionIOstuff.getFactionList().entrySet()){
            Faction value = (Faction) mapElement.getValue();
            int number = value.getMembers().size();
            response += (value.getPrefix() + " (" + String.valueOf(number) + "), \n");
        }

        if(player != null) player.sendMessage(response);
        if(log != null) log.info(response);

        return true;
    }
}