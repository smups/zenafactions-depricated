package ZenaCraft.commands;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

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

        return claimChunkMethod(player);
    }

    private boolean claimChunkMethod(Player player){
        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        MarkerSet markerSet = App.getMarkerSet();
        String fQCName = "X" + String.valueOf(chunk.getX()/100) + "Z" + String.valueOf(chunk.getZ()/100);

        FactionQChunk fQC = App.factionIOstuff.getFQC(fQCName);
        byte[][] chunkData = fQC.getChunkData();
        int ownerID = chunkData[Math.abs(chunk.getX() % 100)][Math.abs(chunk.getZ() % 100)];

        if (ownerID != -1){
            player.sendMessage(App.zenfac + ChatColor.DARK_RED + "This chunk is already claimed!");
            return true;
        }
        chunkData[Math.abs(chunk.getX() % 100)][Math.abs(chunk.getZ() % 100)] = player.getMetadata("factionID").get(0).asByte();

        //do the dynmap stuff
        AreaMarker marker = markerSet.createAreaMarker(String.valueOf(chunk.getX()) + String.valueOf(chunk.getZ()), player.getMetadata("faction").get(0).asString(), true, player.getWorld().getName(), new double[] {chunk.getX()*16, chunk.getX()*16 + 16}, new double[] {chunk.getZ()*16, chunk.getZ()*16 + 16}, true);
        marker.setFillStyle(0.1, 0x42f4f1);
        marker.setLineStyle(1, 0.2, 0x42f4f1);
        player.sendMessage(App.zenfac + "Chunk Claimed!");

        return true;
    }
}
