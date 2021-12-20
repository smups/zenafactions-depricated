package ZenaCraft.commands.warps;

import java.util.List;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class SetWarpRequirement extends TemplateCommand{

    public SetWarpRequirement() {super(2);}

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

        int rankreq = 1;
        if (change.getRankReq() < rankreq) rankreq = change.getRankReq();
        
        if (faction.getPlayerRank(player) > rankreq) return invalidRank(player, rankreq);

        for(int i = 0; i + 1 < faction.getRanks().length; i++){
            if(faction.getRanks()[i].equals(args[1])){
                change.setRankReq(i);
                player.sendMessage(App.zenfac + "Changed rank requirement for warp" +
                    change.getName() + " to: " + ChatColor.BOLD + faction.getRanks()[i]);
                return true;
            }
        }

        player.sendMessage(App.zenfac + ChatColor.RED + "That rank doesn't exist!");
        return true;
    }
}