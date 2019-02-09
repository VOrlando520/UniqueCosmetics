package io.github.teitss.uniquecosmetics.listeners;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import io.github.teitss.uniquecosmetics.UniqueCosmetics;
import io.github.teitss.uniquecosmetics.data.UCKeys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class InteractEntityListener {


    @Listener
    public void onEntityInteract(InteractEntityEvent.Primary.MainHand e, @Root Player player) {
        player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item ->
                item.get(UCKeys.ITEMID).ifPresent(itemID -> {
                    if (e.getTargetEntity() instanceof EntityPixelmon) {
                        Pokemon pokemon = ((EntityPixelmon) e.getTargetEntity()).getPokemonData();
                        UniqueCosmetics.COSMETIC_REGISTRY.get(itemID).onInteract(pokemon, player, item);
                    }
                })
        );
    }

}
