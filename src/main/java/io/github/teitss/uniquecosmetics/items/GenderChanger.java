package io.github.teitss.uniquecosmetics.items;

import com.pixelmonmod.pixelmon.api.dialogue.Choice;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import io.github.teitss.uniquecosmetics.Config;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;

public class GenderChanger extends CosmeticItem {

    @Override
    public void onInteract(Pokemon pokemon, Player player, ItemStack itemStack) {

        if (player.getUniqueId().equals(pokemon.getOwnerPlayerUUID())) {
            player.sendMessage(Config.getMessageAsText("error.notowner"));
            return;
        }

        if (pokemon.isInRanch())
            return;

        if (Gender.None == pokemon.getGender()) {
            player.sendMessage(Config.getMessageAsText("error.nogender"));
            return;
        }

        Gender desiredGender = Gender.Male;

        if (Gender.Male == pokemon.getGender())
            desiredGender = Gender.Female;

        askForConfirmation(pokemon, player, itemStack, desiredGender);

    }

    private void askForConfirmation(Pokemon pokemon, Player player, ItemStack itemStack, Gender desiredGender) {

        Dialogue confirmationDialogue = Dialogue.builder()
                .setName(Config.getMessage("gui.confirmation.gender.title"))
                .setText(Config.getMessageWithPlaceholders("gui.confirmation.gender.description", "%gender%",
                        desiredGender.getLocalizedName()))
                .addChoice(Choice.builder()
                        .setText(Config.getMessage("gui.confirmation.yes"))
                        .setHandle(event -> reassignGender(pokemon, player, itemStack, desiredGender))
                        .build())
                .addChoice(Choice.builder().setText(Config.getMessage("gui.confirmation.no")).build())
                .build();
        Dialogue.setPlayerDialogueData((EntityPlayerMP) player, Collections.singletonList(confirmationDialogue), true);
    }

    private void reassignGender(Pokemon pokemon, Player player, ItemStack itemStack, Gender desiredGender) {
        if (consumeItem(itemStack, player)) {
            pokemon.setGender(desiredGender);
            player.sendMessage(Config.getMessageAsText("success.gender"));
        }
    }

}
