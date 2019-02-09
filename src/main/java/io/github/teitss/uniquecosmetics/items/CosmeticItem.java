package io.github.teitss.uniquecosmetics.items;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

public abstract class CosmeticItem {

    /**
     * Handles the interaction with pokemon.
     *
     * @param pokemon   The pokemon which player interacted.
     * @param player    The player who clicked.
     * @param itemStack The item used when interaction occured.
     */
    public abstract void onInteract(Pokemon pokemon, Player player, ItemStack itemStack);

    /**
     * Consumes the given {@link ItemStack} from {@link Player}'s {@link MainPlayerInventory}.
     *
     * @param itemStack The Item Stack that will be consumed.
     * @param player    The player who will have the ItemStack deducted from it's inventory..
     * @return true if ItemStack was consumed with success.
     */
    protected boolean consumeItem(ItemStack itemStack, Player player) {
        MainPlayerInventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
        return inv.query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(itemStack)).poll(1).isPresent();
    }

}
