package ZenaCraft.commands.faction;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;
import ZenaCraft.objects.pItem;
import net.milkbowl.vault.economy.Economy;

public class BuyFactionBanner extends TemplateCommand{

    public BuyFactionBanner() {super(0);}

    @Override
    protected boolean run() {
        Faction f = App.factionIOstuff.getPlayerFaction(player);
        Economy econ = App.getEconomy();
        Plugin plugin = App.getPlugin(App.class);

        double bannerprice = plugin.getConfig().getDouble("Banner Price");

        if (!econ.has(player, bannerprice)) return insufficientFunds(player);

        pItem banner = f.getBanner();

        if (banner == null){
            player.sendMessage(App.zenfac + ChatColor.RED + "Your faction doesn't have a banner!");
            return true;
        }

        player.getInventory().addItem(banner.getItemStack());
        econ.withdrawPlayer(player, bannerprice);
        player.sendMessage(App.zenfac + ChatColor.RESET + "Purchased " + f.getPrefix() +
            "'s banner" + ChatColor.RESET + " for: " + formatMoney(bannerprice));
        return true;
    }    
}