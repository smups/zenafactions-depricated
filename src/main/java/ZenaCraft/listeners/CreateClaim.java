package ZenaCraft.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ZenaCraft.App;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;

public class CreateClaim implements Listener {

    @EventHandler
    void onCreateClaim(ClaimCreatedEvent event){
        Player player = (Player) event.getCreator();
        Claim claim = event.getClaim();

        int condition = 0;

        for (Chunk chunk : claim.getChunks()){
            int chunkX = (int) chunk.getX();
            int chunkZ = (int) chunk.getZ();
            String FQCName = App.factionIOstuff.calcFQCName(chunkX, chunkZ, null, null);

            byte[][] chunkData = App.factionIOstuff.getFQC(FQCName).getChunkData();
            int ownerID = chunkData[Math.abs(chunkX % 100)][Math.abs(chunkZ % 100)];
            if (ownerID == -1) continue; //anyone can claim in neutral territory
            byte playerFaction = (byte) App.factionIOstuff.getPlayerFaction(player).getID();
            if (ownerID != playerFaction) condition += 1;
        }

        if(claim.isAdminClaim()) condition = 0;

        if (condition != 0){
            event.setCancelled(true);
            player.sendMessage(App.zenfac + ChatColor.RED + "Claim not within faction borders!");
        }
    }
    
}