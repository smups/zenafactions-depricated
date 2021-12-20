package ZenaCraft.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class pItem implements Serializable{
    //versie van Location die wel persistent is
    static final long serialVersionUID = 100L;

    private final String data;
    private final UUID id;

    private transient ItemStack is;

    public pItem(ItemStack is){
        id = UUID.randomUUID();
        this.is = is;
        data = this.itemStackArrayToBase64(new ItemStack[] {this.is});
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        //default
        in.defaultReadObject();

        //update the thing now
        this.is = itemStackArrayFromBase64(data)[0];
    }
    
    private String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
    	try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            // Write the size of the inventory
            dataOutput.writeInt(items.length);
            
            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }
            
            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    private ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
    
            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
            	items[i] = (ItemStack) dataInput.readObject();
            }
            
            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public ItemStack getItemStack(){
        return this.is;
    }
    public UUID getID(){
        return this.id;
    }
    public void giveToPlayer(Player player, int amount){
        ItemStack is = this.is.clone();
        is.setAmount(amount);
        player.getInventory().addItem(is);
    }
}