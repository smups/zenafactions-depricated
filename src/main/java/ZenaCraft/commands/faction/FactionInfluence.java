package ZenaCraft.commands.faction;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class FactionInfluence extends TemplateCommand {

    public FactionInfluence() {super(0);}

    @Override
    protected boolean run() {
        Faction faction = (Faction) App.factionIOstuff.getPlayerFaction(player);
        String influence = String.valueOf(faction.getInfluence());
        player.sendMessage(App.zenfac + ChatColor.WHITE + faction.getName() + " has: " + influence);
        return true;
    }
}