package ZenaCraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;

public class toggleAutoClaim implements CommandExecutor{

    Plugin plugin = App.getPlugin(App.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;
        player.setMetadata("autoClaiming", new FixedMetadataValue(plugin, player.getMetadata("autoClaiming").get(0).asBoolean() == false));
        player.sendMessage(App.zenfac + "toggled autoclaiming!");
        return true;
    }
}