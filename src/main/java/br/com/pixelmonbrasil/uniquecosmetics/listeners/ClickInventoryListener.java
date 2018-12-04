package br.com.pixelmonbrasil.uniquecosmetics.listeners;

import br.com.pixelmonbrasil.uniquecosmetics.data.UCKeys;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

public class ClickInventoryListener {

    //Fix for click->drop/drop->click issue which bypass item consume
    @Listener
    public void onItemDrop(ClickInventoryEvent.Drop e, @Root Player player) {
        // Get dropped items and check for custom data
        for (SlotTransaction st : e.getTransactions()) {
            if (st.getOriginal().get(UCKeys.ITEMID).isPresent()) {

                //If there are a pokémon entity within 10 blocks nearby owned by the player
                //and is not in ranch drop will be cancelled.
                boolean mustCancel = player.getNearbyEntities(10).removeIf(entity ->
                        entity instanceof EntityPixelmon &&
                                ((EntityPixelmon) entity).getOwnerId() == player.getUniqueId() &&
                                !((EntityPixelmon) entity).isInRanchBlock);

                e.setCancelled(mustCancel);
                break;
            }
        }
    }

}
