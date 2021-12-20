package ZenaCraft.commands;

import org.bukkit.ChatColor;
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
        String radius;
        if (args.length == 1){
            player.setMetadata("autoClaimingRadius", new FixedMetadataValue(plugin, Integer.parseInt(args[0])));
            radius = args[0];
        }
        else radius = player.getMetadata("autoClaimingRadius").get(0).asString();
        player.sendMessage(App.zenfac + "toggled autoclaiming!, brushsize set to: " + ChatColor.GREEN + radius);
        return true;
    }
}