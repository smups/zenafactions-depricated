package ZenaCraft.commands;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ZenaCraft.App;
import ZenaCraft.objects.FactionQChunk;
import net.md_5.bungee.api.ChatColor;

public class claimChunk  implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            return true;
        }
        Player player = (Player) sender;

        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        String fQCName = "X" + String.valueOf(chunk.getX()/100) + "Z" + String.valueOf(chunk.getZ()/100);

        FactionQChunk fQC = App.loadedFQChunks.get(fQCName);
        byte[][] chunkData = fQC.getChunkData();
        int ownerID = chunkData[Math.abs(chunk.getX() % 100)][Math.abs(chunk.getZ() % 100)];

        if (ownerID != -1){
            player.sendMessage(App.zenfac + ChatColor.DARK_RED + "This chunk is already claimed!");
            return true;
        }
        chunkData[Math.abs(chunk.getX() % 100)][Math.abs(chunk.getZ() % 100)] = player.getMetadata("factionID").get(0).asByte();
        player.sendMessage(App.zenfac + "Chunk Claimed!");
        return true;
    }
}
