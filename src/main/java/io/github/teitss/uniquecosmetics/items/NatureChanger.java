package io.github.teitss.uniquecosmetics.items;

import com.pixelmonmod.pixelmon.api.dialogue.Choice;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumNature;
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

public class NatureChanger extends CosmeticItem {

    private HashMap<Integer, Pair<Integer, Integer>> paginationMap = new HashMap<>();

    public NatureChanger() {
        //Organize pagination map accordingly with configuration.
        int choicesPerPage = Config.getChoicesPerPage();
        //We exclude the current nature of pokemon
        int numberOfPages = (EnumNature.values().length - 1) / choicesPerPage;
        //If the page number is not an integer just add one more.
        if (((EnumNature.values().length - 1) % choicesPerPage) != 0)
            numberOfPages++;
        //Mapping the position number for each page, then just get the position from Enum.values()
        for (int i = 1; i <= numberOfPages; i++) {
            paginationMap.put(i, new ImmutablePair<>((i * choicesPerPage) - choicesPerPage, i * choicesPerPage));
        }
    }

    @Override
    public void onInteract(Pokemon pokemon, Player player, ItemStack itemStack) {
        if (player.getUniqueId() != pokemon.getOwnerPlayerUUID()) {
            player.sendMessage(Config.getMessageAsText("error.notowner"));
            return;
        }

        if (pokemon.isInRanch())
            return;

        Dialogue.setPlayerDialogueData(
                (EntityPlayerMP) player,
                Collections.singletonList(displayAvailableNatures(pokemon, 1, itemStack)),
                true);
    }

    private Dialogue displayAvailableNatures(Pokemon pokemon, int pageIndex, ItemStack itemStack) {
        ArrayList<String> list = new ArrayList<>();
        for (EnumNature nature : EnumNature.values()) {
            if (nature != pokemon.getNature())
                list.add(nature.getLocalizedName());
        }
        return Dialogue.builder()
                .setName(Config.getMessage("gui.nature.title"))
                .setText(Config.getMessage("gui.nature.description"))
                .setChoices(buildPage(pageIndex, list, pokemon, itemStack))
                .build();
    }

    private ArrayList<Choice> buildPage(int pageIndex, ArrayList<String> choicesList, Pokemon pokemon, ItemStack itemStack) {
        ArrayList<Choice> list = new ArrayList<>();
        Pair<Integer, Integer> bounds = paginationMap.get(pageIndex);
        for (int i = bounds.getLeft(); i < bounds.getRight(); i++) {
            if (i < choicesList.size()) {
                String natureName = choicesList.get(i);
                list.add(Choice.builder()
                        .setText(natureName)
                        .setHandle(choice -> askForConfirmation(pokemon, (Player) choice.player, itemStack, natureName))
                        .build());
            }
        }
        if (pageIndex < paginationMap.size())
            list.add(Choice.builder()
                    .setText("Próxima página")
                    .setHandle(choice -> choice.reply(displayAvailableNatures(pokemon, pageIndex + 1, itemStack)))
                    .build());
        if (pageIndex > 1) {
            list.add(Choice.builder()
                    .setText("Página anterior")
                    .setHandle(choice -> choice.reply(displayAvailableNatures(pokemon, pageIndex - 1, itemStack)))
                    .build());
        }
        return list;
    }

    private void askForConfirmation(Pokemon pokemon, Player player, ItemStack itemStack, String natureName) {
        Dialogue confirmationDialogue = Dialogue.builder()
                .setName(Config.getMessage("gui.confirmation.nature.title"))
                .setText(Config.getMessageWithPlaceholders("gui.confirmation.nature.title", "%nature%", natureName))
                .addChoice(Choice.builder().setText(Config.getMessage("gui.confirmation.yes")).setHandle(event ->
                        changePokemonNature(pokemon, player, itemStack, natureName)
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

    private void changePokemonNature(Pokemon pokemon, Player player, ItemStack itemStack, String choice) {
        if (consumeItem(itemStack, player)) {
            pokemon.setNature(EnumNature.valueOf(choice));
            player.sendMessage(Config.getMessageAsText("success.nature"));
        }
    }

}
