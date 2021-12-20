package ZenaCraft.commands.warps;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.Warp;

public class WarpPlayer extends TemplateCommand{

    public WarpPlayer() {super(1);}

    @Override
    protected boolean run() {
        Faction f = App.factionIOstuff.getPlayerFaction(player);
        
        for(Warp w : f.getWarpList()){
            if(w.getName().equals(args[0])){
                f.getWarp(args[0]).warpPlayer(player);
                return true;
            }
        }

        Faction df = App.factionIOstuff.getFaction(0);

        for(Warp w : df.getWarpList()){
            if(w.getName().equals(args[0])){
                df.getWarp(args[0]).warpPlayer(player);
                return true;
            }
        }

        player.sendMessage(App.zenfac + ChatColor.RED + "No such warp exists!");
        return true;
    }
}