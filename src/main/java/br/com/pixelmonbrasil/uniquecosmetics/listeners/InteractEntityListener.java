package br.com.pixelmonbrasil.uniquecosmetics.listeners;

import br.com.pixelmonbrasil.uniquecosmetics.Config;
import br.com.pixelmonbrasil.uniquecosmetics.UniqueCosmetics;
import br.com.pixelmonbrasil.uniquecosmetics.data.UCKeys;
import br.com.pixelmonbrasil.uniquecosmetics.dialogues.DialogueManager;
import com.pixelmonmod.pixelmon.api.dialogue.Choice;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

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

        if (ep.isInRanchBlock)
            return;

        if (ep.getIsShiny()) {
            player.sendMessage(Config.getMessageAsText("error.alreadyshiny"));
            return;
        }

        askForShinyConfirmation(ep, player, itemStackSnapshot);

    }

    private void handleChangeWithGui(EntityPixelmon ep, Player player, DialogueManager.EnumChange change, ItemStackSnapshot itemStackSnapshot) {
        if (player.getUniqueId() != ep.getOwnerId()) {
            player.sendMessage(Config.getMessageAsText("error.notowner"));
            return;
        }

        if (ep.isInRanchBlock)
            return;

        Dialogue.setPlayerDialogueData(
                (EntityPlayerMP) player,
                Collections.singletonList(UniqueCosmetics.getInstance().getDialogueManager().createDialogue(ep, 1, change, itemStackSnapshot)),
                true);
    }

    //Return true if item was consumed.
    private boolean consumeItem(ItemStackSnapshot itemStackSnapshot, Player player) {
        ItemStack itemStack = itemStackSnapshot.createStack();
        PlayerInventory inv = (PlayerInventory) player.getInventory();
        return inv.query(QueryOperationTypes.ITEM_STACK_EXACT.of(itemStack)).poll(1).isPresent();
    }

    private void doneShinyTransformation(EntityPixelmon ep, Player player, ItemStackSnapshot itemStackSnapshot) {
        if (consumeItem(itemStackSnapshot, player)) {
            PokemonSpec.from("shiny").apply(ep);
            player.sendMessage(Config.getMessageAsText("success.shiny"));
        }
    }

    private void askForShinyConfirmation(EntityPixelmon ep, Player player, ItemStackSnapshot itemStackSnapshot) {
        Dialogue confirmationDialogue = Dialogue.builder()
                .setName("Confirmação de ação")
                .setText("Você tem certeza que deseja transformar seu pokémon em shiny?")
                .addChoice(Choice.builder().setText("Sim").setHandle(dialogueChoiceEvent ->  {
                    doneShinyTransformation(ep, player, itemStackSnapshot);
                }).build())
                .addChoice(Choice.builder().setText("Não").build())
                .build();
        Dialogue.setPlayerDialogueData((EntityPlayerMP)player, Collections.singletonList(confirmationDialogue), true);
    }

}
