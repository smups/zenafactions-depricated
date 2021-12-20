package ZenaCraft.commands.warps;

import java.util.List;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class SetWarpTax extends TemplateCommand{

    public SetWarpTax() {super(2, true, 1);}

    @Override
    protected boolean run() {
        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        List<Warp> wList = faction.getWarpList();

        if (wList.size() == 0){
            player.sendMessage(App.zenfac + ChatColor.RED + "your faction doesn't have any warps!");
            return true;
        }

        Warp change = null;

        for(Warp w : wList){
            if (w.getName().equals(args[0])) change = w;
        }

        if (change == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "That warp doesn't exist!");
            return true;
        }
        
        if(!hasPerm(player)) return invalidRank(player);
        if (!faction.getPlayerRank(player).hasPerm(change.getPerm())) return invalidRank(player);

        Double factiontax = formatDouble(args[1]);
        if (factiontax == null) return invalidSyntax(player);

        change.setFactionTax(factiontax/100);

        player.sendMessage(App.zenfac + "Changed factiontax for warp " + 
        change.getName() + " to: " + ChatColor.BOLD + "" + ChatColor.GREEN +
        formatPercent(change.getFactionTax()) + "%");

    return true;
    }
}