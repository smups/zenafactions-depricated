package ZenaCraft.events;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import ZenaCraft.App;
import ZenaCraft.objects.FactionQChunk;

public class PlayerMove implements Listener{

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        
        Location oldLocation = event.getFrom();
        Location newLocation = event.getTo();

        Chunk newChunk = newLocation.getChunk();
        Chunk oldChunk = oldLocation.getChunk();

        if (oldChunk.getX() != newChunk.getX() || oldChunk.getZ() != newChunk.getZ()){
            String oldFQChunkName = "X" + String.valueOf(oldChunk.getX()/100) + "Z" + String.valueOf(oldChunk.getZ()/100);
            String newFQChunkName = "X" + String.valueOf(newChunk.getX()/100) + "Z" + String.valueOf(newChunk.getZ()/100);

            //player.sendMessage(App.zenfac + "Moved from FQC: [" + oldFQChunkName + "] to FQC: [" + newFQChunkName +"]");
            byte oldOwnerID = App.loadedFQChunks.get(newFQChunkName).getChunkData()[Math.abs(oldChunk.getX() % 100)][Math.abs(oldChunk.getZ() % 100)];

            if(!oldFQChunkName.equals(newFQChunkName)){
                App.loadedFQChunks.put(newFQChunkName, new FactionQChunk(newFQChunkName, player, new double[] {newLocation.getX(), newLocation.getY()}));
                player.sendMessage(App.zenfac + "Entering new FQChunk");

                FactionQChunk oldFactionQChunk = (FactionQChunk) App.loadedFQChunks.get(oldFQChunkName);
                if(oldFactionQChunk.getOnlinePlayers().size() == 1){
                    oldFactionQChunk.saveFQChunkData();
                    App.loadedFQChunks.remove(oldFQChunkName);
                }
                else{
                    oldFactionQChunk.removeOnlinePlayer(player);
                }

                oldOwnerID = App.loadedFQChunks.get(oldFQChunkName).getChunkData()[Math.abs(oldChunk.getX() % 100)][Math.abs(oldChunk.getZ() % 100)];
            }

            byte newOwnerID = App.loadedFQChunks.get(newFQChunkName).getChunkData()[Math.abs(newChunk.getX() % 100)][Math.abs(newChunk.getZ() % 100)];
            if (newOwnerID != oldOwnerID){
                if(newOwnerID == -1)
                {
                    player.sendTitle("Entering wilderness", "you may claim this", 10, 70, 10);
                }
                else{
                    player.sendTitle("Entering sqrrt", "you may claim this", 10, 70, 10);
                }
            }
        }     
    }
}