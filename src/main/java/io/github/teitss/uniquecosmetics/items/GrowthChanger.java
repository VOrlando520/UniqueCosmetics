package io.github.teitss.uniquecosmetics.items;

import com.pixelmonmod.pixelmon.api.dialogue.Choice;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import io.github.teitss.uniquecosmetics.Config;
import io.github.teitss.uniquecosmetics.UniqueCosmetics;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GrowthChanger extends CosmeticItem {
    private HashMap<Integer, Pair<Integer, Integer>> paginationMap = new HashMap<>();

    public GrowthChanger() {
        //Organize pagination map accordingly with configuration.
        int choicesPerPage = Config.getChoicesPerPage();
        //We exclude the current Growth of pokemon
        int numberOfPages = (EnumGrowth.values().length - 1) / choicesPerPage;
        //If the page number is not an integer just add one more.
        if (((EnumGrowth.values().length - 1) % choicesPerPage) != 0)
            numberOfPages++;
        //Mapping the position number for each page, then just get the position from Enum.values()
        for (int i = 1; i <= numberOfPages; i++) {
            paginationMap.put(i, new ImmutablePair<>((i * choicesPerPage) - choicesPerPage, i * choicesPerPage));
        }
    }

    @Override
    public void onInteract(Pokemon pokemon, Player player, ItemStack itemStack) {
        if (!player.getUniqueId().equals(pokemon.getOwnerPlayerUUID())) {
            player.sendMessage(Config.getMessageAsText("error.notowner"));
            return;
        }

        if (pokemon.isInRanch())
            return;

        Dialogue.setPlayerDialogueData(
                (EntityPlayerMP) player,
                Collections.singletonList(displayAvailableGrowths(pokemon, 1, itemStack)),
                true);
    }

    private Dialogue displayAvailableGrowths(Pokemon pokemon, int pageIndex, ItemStack itemStack) {
        ArrayList<String> list = new ArrayList<>();
        for (EnumGrowth growth : EnumGrowth.values()) {
            if (growth != pokemon.getGrowth())
                list.add(growth.getLocalizedName());
        }
        return Dialogue.builder()
                .setName(Config.getMessage("gui.growth.title"))
                .setText(Config.getMessage("gui.growth.description"))
                .setChoices(buildPage(pageIndex, list, pokemon, itemStack))
                .build();
    }

    private ArrayList<Choice> buildPage(int pageIndex, ArrayList<String> choicesList, Pokemon pokemon, ItemStack itemStack) {
        ArrayList<Choice> list = new ArrayList<>();
        Pair<Integer, Integer> bounds = paginationMap.get(pageIndex);
        for (int i = bounds.getLeft(); i < bounds.getRight(); i++) {
            if (i < choicesList.size()) {
                String growthName = choicesList.get(i);
                list.add(Choice.builder()
                        .setText(growthName)
                        .setHandle(choice -> askForConfirmation(pokemon, (Player) choice.player, itemStack, growthName))
                        .build());
            }
        }
        if (pageIndex < paginationMap.size())
            list.add(Choice.builder()
                    .setText("Next page")
                    .setHandle(choice -> choice.reply(displayAvailableGrowths(pokemon, pageIndex + 1, itemStack)))
                    .build());
        if (pageIndex > 1) {
            list.add(Choice.builder()
                    .setText("Previous page")
                    .setHandle(choice -> choice.reply(displayAvailableGrowths(pokemon, pageIndex - 1, itemStack)))
                    .build());
        }
        return list;
    }

    private void askForConfirmation(Pokemon pokemon, Player player, ItemStack itemStack, String growthName) {
        Dialogue confirmationDialogue = Dialogue.builder()
                .setName(Config.getMessage("gui.confirmation.growth.title"))
                .setText(Config.getMessageWithPlaceholders("gui.confirmation.growth.description", "%growth%", growthName))
                .addChoice(Choice.builder().setText(Config.getMessage("gui.confirmation.yes")).setHandle(event ->
                        changePokemonCaughtBall(pokemon, player, itemStack, growthName)
                ).build())
                .addChoice(Choice.builder().setText(Config.getMessage("gui.confirmation.no")).build())
                .build();
        //We need this, otherwise the new Dialogue won't displays.
        Sponge.getScheduler().createTaskBuilder()
                .name("OpenConfirmation")
                .delayTicks(1)
                .execute(task ->
                        Dialogue.setPlayerDialogueData((EntityPlayerMP) player, Collections.singletonList(confirmationDialogue), true)
                )
                .submit(UniqueCosmetics.INSTANCE);
    }

    private void changePokemonCaughtBall(Pokemon pokemon, Player player, ItemStack itemStack, String choice) {
        if (consumeItem(itemStack, player)) {
            pokemon.setGrowth(EnumGrowth.valueOf(choice));
            player.sendMessage(Config.getMessageAsText("success.growth"));
        }
    }
}
