package ZenaCraft.commands.financial;

import org.bukkit.ChatColor;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class FactionBalance extends TemplateCommand {

    public FactionBalance() {super(0);}

    @Override
    protected boolean run() {
        Faction faction = App.factionIOstuff.getPlayerFaction(player);
        player.sendMessage(App.zenfac + faction.getPrefix() +  ChatColor.WHITE +
            " has: " + formatMoney(faction.getBalance()));
        return true;
    }
}