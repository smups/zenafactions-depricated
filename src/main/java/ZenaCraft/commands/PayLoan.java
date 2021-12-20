package ZenaCraft.commands;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.Faction;

public class PayLoan implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 2) return App.invalidSyntax(player);

        Faction faction = null;

        for(Entry mEntry : App.factionIOstuff.getFactionList().entrySet()){
            Faction f = (Faction) mEntry.getValue();
            if (f.getName().equals(args[0])) faction = f;
        }

        if (faction == null){
            player.sendMessage(App.zenfac + ChatColor.RED + " faction " + args[0] + " not found!");
            return true;
        }

        try{
            faction.getRunningLoans().get(Integer.parseInt(args[1])).payLoan();
        }
        catch (Exception e){
            return App.invalidSyntax(player);
        }

        return true;
    }
}
