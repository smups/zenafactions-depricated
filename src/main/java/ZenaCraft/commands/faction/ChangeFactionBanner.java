package ZenaCraft.commands.faction;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class ChangeFactionBanner extends TemplateCommand{

    public ChangeFactionBanner() {super(0);}

    @Override
    protected boolean run() {
        PlayerInventory inv = player.getInventory();
        ItemStack hand = inv.getItemInMainHand().clone();

        hand.setAmount(1);

        if (!(hand.getItemMeta() instanceof BannerMeta)){
            player.sendMessage(App.zenfac + ChatColor.RED + "The item you're holding" +
            " is not a banner!");
            return true;
        }

        Faction f = App.factionIOstuff.getPlayerFaction(player);

        ItemMeta im = hand.getItemMeta().clone();
        im.setDisplayName(f.getPrefix() + "'s banner");

        hand.setItemMeta(im);

        if(f.getPlayerRank(player) != 0) return invalidRank(player, 0);

        f.setBanner(hand);
        player.sendMessage(App.zenfac + "Changed faction banner!");
        return true;
    }    
}