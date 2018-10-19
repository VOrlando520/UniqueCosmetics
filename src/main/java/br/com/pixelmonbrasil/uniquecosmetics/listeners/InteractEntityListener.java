package br.com.pixelmonbrasil.uniquecosmetics.listeners;

import br.com.pixelmonbrasil.uniquecosmetics.Config;
import br.com.pixelmonbrasil.uniquecosmetics.UniqueCosmetics;
import br.com.pixelmonbrasil.uniquecosmetics.data.UCKeys;
import br.com.pixelmonbrasil.uniquecosmetics.dialogues.DialogueManager;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Collections;

public class InteractEntityListener {



    @Listener
    public void onEntityInteract(InteractEntityEvent.Primary.MainHand e, @Root Player player) {
        player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
            item.get(UCKeys.ITEMID).ifPresent(itemID -> {
                if (e.getTargetEntity() instanceof EntityPixelmon) {
                    EntityPixelmon ep = (EntityPixelmon) e.getTargetEntity();
                    switch (itemID) {
                        case "shinyTransformation": {
                            handleShiny(ep, player, item.createSnapshot());
                            break;
                        }
                        case "natureChanger": {
                            handleChangeWithGui(ep, player, DialogueManager.EnumChange.NATURE_CHANGE, item.createSnapshot());
                            break;
                        }
                        case "pokeballChanger": {
                            handleChangeWithGui(ep, player, DialogueManager.EnumChange.POKEBALL_CHANGE, item.createSnapshot());
                            break;
                        }
                    }
                }
            });
        });
    }

    private void handleShiny(EntityPixelmon ep, Player player, ItemStackSnapshot itemStackSnapshot) {
        if (player.getUniqueId() != ep.getOwnerId()) {
            player.sendMessage(Config.getMessageAsText("error.notowner"));
            return;
        }

        if (ep.getIsShiny()) {
            player.sendMessage(Config.getMessageAsText("error.alreadyshiny"));
            return;
        }

        PokemonSpec.from("shiny").apply(ep);
        player.sendMessage(Config.getMessageAsText("success.shiny"));
        consumeItem(itemStackSnapshot, player);
    }

    private void handleChangeWithGui(EntityPixelmon ep, Player player, DialogueManager.EnumChange change, ItemStackSnapshot itemStackSnapshot) {
        if (player.getUniqueId() != ep.getOwnerId()) {
            player.sendMessage(Config.getMessageAsText("error.notowner"));
            return;
        }

        Dialogue.setPlayerDialogueData(
                (EntityPlayerMP) player,
                Collections.singletonList(UniqueCosmetics.getInstance().getDialogueManager().createDialogue(ep, 1, change, itemStackSnapshot)),
                true);
    }

    private void consumeItem(ItemStackSnapshot itemStackSnapshot, Player player) {
        if (itemStackSnapshot.getQuantity() == 1) {
            player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.of(ItemTypes.AIR, 1));
        } else {
            ItemStack is = itemStackSnapshot.createStack();
            is.setQuantity(itemStackSnapshot.getQuantity() - 1);
            player.setItemInHand(HandTypes.MAIN_HAND, is);
        }
    }

}
