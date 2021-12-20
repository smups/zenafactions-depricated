package ZenaCraft.commands;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;

public class ListLoadedFQCs extends TemplateCommand{

    public ListLoadedFQCs() {super(0);}

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
        String response = App.zenfac + "Loaded FQC's: ";

        for (Map.Entry mapElement : App.factionIOstuff.getLoadedFQChunks().entrySet()){
            String key = (String) mapElement.getKey();
            response += (key + ", ");
        }
        
        if (player != null) player.sendMessage(response);
        if (log != null) log.info(response);

        return true;
    }
}