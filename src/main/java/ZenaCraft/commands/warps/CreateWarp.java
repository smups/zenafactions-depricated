package ZenaCraft.commands.warps;

import org.bukkit.ChatColor;
import org.bukkit.metadata.FixedMetadataValue;

import ZenaCraft.App;
import ZenaCraft.commands.TemplateCommand;
import ZenaCraft.objects.Faction;

public class CreateWarp extends TemplateCommand{

    public CreateWarp() {super(1);}

    @Override
    protected boolean run() {
        Faction faction = App.factionIOstuff.getPlayerFaction(player);

        if(faction.getPlayerRank(player) > 1) return invalidRank(player, 1);

        double createCost = App.getPlugin(App.class).getConfig().getDouble("warpCreationCost");
        double warpScale = App.getPlugin(App.class).getConfig().getDouble("warpScale");
        createCost = createCost*Math.pow(faction.getWarpList().size(), warpScale);

        if ((faction.getBalance() < createCost) && (faction.getID() != 0)) return insufficientFactionFunds(player);

        if (faction.hasWarp(args[0])){
            player.sendMessage(App.zenfac + ChatColor.RED + "This warp already exists!");
            return true;
        }

        if (!player.hasMetadata("createWarp") || !player.getMetadata("createWarp").get(0).asBoolean()){
            player.sendMessage(App.zenfac + ChatColor.WHITE + "are you sure you want to create a new warp?" +
            " This costs " + formatMoney(createCost) + ChatColor.RESET + ". Retype this command to confirm!");
            player.setMetadata("createWarp", new FixedMetadataValue(App.getPlugin(App.class), true));
            return true;
        }

        if (faction.getID() !=0 ) faction.removeBalance(createCost);

        faction.addWarp(player.getLocation(), args[0]);
        player.sendMessage(App.zenfac + ChatColor.GREEN + "Warp created!");
        player.setMetadata("createWarp", new FixedMetadataValue(App.getPlugin(App.class), false));
    
        return true;
        }
}