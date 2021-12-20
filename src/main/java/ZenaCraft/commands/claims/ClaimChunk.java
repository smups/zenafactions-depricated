package ZenaCraft.commands.claims;

import org.bukkit.ChatColor;
import org.bukkit.World;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;

public class ClaimChunk  extends TemplateCommand{

    public ClaimChunk() {super(1);}

    @Override
    protected boolean run() {
        Integer radius = formatInt(args[0], player);
        if (radius == null) return true;

        if (!player.getWorld().getEnvironment().equals(World.Environment.NORMAL)){
            player.sendMessage(App.zenfac + ChatColor.RED + "factions are disabled in the Nether and the End");
            return true;
        }

        App.factionIOstuff.claimChunks(player, null, null, radius, null);
        return true;
    }
}