package io.github.teitss.uniquecosmetics.items;

import com.pixelmonmod.pixelmon.api.dialogue.Choice;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import io.github.teitss.uniquecosmetics.Config;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;

public class ShinyChanger extends CosmeticItem {

    @Override
    public void onInteract(Pokemon pokemon, Player player, ItemStack itemStack) {

        if (!player.getUniqueId().equals(pokemon.getOwnerPlayerUUID())) {
            player.sendMessage(Config.getMessageAsText("error.notowner"));
            return;
        }

        if (pokemon.isInRanch())
            return;

        if (pokemon.isShiny()) {
            player.sendMessage(Config.getMessageAsText("error.alreadyshiny"));
            return;
        }

        askForConfirmation(pokemon, player, itemStack);

    }

    private void askForConfirmation(Pokemon ep, Player player, ItemStack itemStack) {
        Dialogue confirmationDialogue = Dialogue.builder()
                .setName(Config.getMessage("gui.confirmation.shiny.title"))
                .setText(Config.getMessage("gui.confirmation.shiny.description"))
                .addChoice(Choice.builder()
                        .setText(Config.getMessage("gui.confirmation.yes"))
                        .setHandle(event -> convertToShiny(ep, player, itemStack))
                        .build())
                .addChoice(Choice.builder().setText(Config.getMessage("gui.confirmation.no")).build())
                .build();
        Dialogue.setPlayerDialogueData((EntityPlayerMP) player, Collections.singletonList(confirmationDialogue), true);
    }

    private void convertToShiny(Pokemon pokemon, Player player, ItemStack itemStack) {
        if (consumeItem(itemStack, player)) {
            pokemon.setShiny(true);
            player.sendMessage(Config.getMessageAsText("success.shiny"));
        }
    }

}
