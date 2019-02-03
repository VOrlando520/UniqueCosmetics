package br.com.pixelmonbrasil.uniquecosmetics.dialogues;

import br.com.pixelmonbrasil.uniquecosmetics.Config;
import br.com.pixelmonbrasil.uniquecosmetics.UniqueCosmetics;
import com.pixelmonmod.pixelmon.api.dialogue.Choice;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DialogueManager {


    private HashMap<EnumChange, HashMap<Integer, int[]>> paginationMap;

    public DialogueManager() {
        paginationMap = new HashMap<>();
        paginationMap.put(EnumChange.NATURE_CHANGE, new HashMap<>());
        paginationMap.put(EnumChange.POKEBALL_CHANGE, new HashMap<>());
        paginationMap.put(EnumChange.GROWTH_CHANGE, new HashMap<>());
        populatePaginationMap(Config.getChoicesPerPage());
    }

    public void reloadPaginationMap() {
        paginationMap.clear();
        paginationMap.put(EnumChange.NATURE_CHANGE, new HashMap<>());
        paginationMap.put(EnumChange.POKEBALL_CHANGE, new HashMap<>());
        paginationMap.put(EnumChange.GROWTH_CHANGE, new HashMap<>());
        populatePaginationMap(Config.getChoicesPerPage());
    }

    private ArrayList<Choice> createChoices(Pokemon pokemon, ArrayList<String> choicesList, int pageIndex, EnumChange change, ItemStackSnapshot itemStackSnapshot) {
        ArrayList<Choice> list = new ArrayList<>();
        int[] bounds = paginationMap.get(change).get(pageIndex).clone();
        for (int i = bounds[0]; i < bounds[1]; i++) {
            if (i < choicesList.size()) {
                String choice = choicesList.get(i);
                list.add(Choice.builder()
                        .setText(choice)
                        .setHandle(choice2 -> handleChoice(choice, pokemon, choice2.player, change, itemStackSnapshot))
                        .build());
            }
        }
        if (pageIndex < paginationMap.get(change).size())
            list.add(Choice.builder()
                    .setText("Próxima página")
                    .setHandle(choice -> choice.reply(createDialogue(pokemon, pageIndex + 1, change, itemStackSnapshot)))
                    .build());
        if (pageIndex > 1) {
            list.add(Choice.builder()
                    .setText("Página anterior")
                    .setHandle(choice -> choice.reply(createDialogue(pokemon, pageIndex - 1, change, itemStackSnapshot)))
                    .build());
        }
        return list;
    }

    public Dialogue createDialogue(Pokemon pokemon, int pageIndex, EnumChange change, ItemStackSnapshot itemStack) {
        ArrayList<String> list = new ArrayList<>();
        switch (change) {
            case NATURE_CHANGE: {
                for (EnumNature nature : EnumNature.values()) {
                    if (pokemon.getNature() != nature)
                        list.add(nature.name());
                }
                return Dialogue.builder()
                        .setName(Config.getMessage("gui.nature.title"))
                        .setText(Config.getMessage("gui.nature.description"))
                        .setChoices(selectPage(pokemon, list, pageIndex, change, itemStack))
                        .build();
            }
            case POKEBALL_CHANGE: {
                for (EnumPokeballs pokeball : EnumPokeballs.values()) {
                    if (pokemon.getCaughtBall() != pokeball)
                        list.add(pokeball.name());
                }
                return Dialogue.builder()
                        .setName(Config.getMessage("gui.pokeball.title"))
                        .setText(Config.getMessage("gui.pokeball.description"))
                        .setChoices(selectPage(pokemon, list, pageIndex, change, itemStack))
                        .build();
            }
            case GROWTH_CHANGE: {
                for (EnumGrowth growth : EnumGrowth.values()) {
                    if (pokemon.getGrowth() != growth)
                        list.add(growth.name());
                }
                return Dialogue.builder()
                        .setName(Config.getMessage("gui.growth.title"))
                        .setText(Config.getMessage("gui.growth.description"))
                        .setChoices(selectPage(pokemon, list, pageIndex, change, itemStack))
                        .build();
            }
            default:
                throw new RuntimeException("Ocorreu um erro ao processar o diálogo de um item " + change.name());
        }
    }

    private void handleChoice(String choice, Pokemon pokemon, EntityPlayerMP player, EnumChange change, ItemStackSnapshot itemStack) {
        if (EnumChange.NATURE_CHANGE == change) {
            askForNatureConfirmation(pokemon, (Player) player, itemStack, choice);
        } else if (EnumChange.POKEBALL_CHANGE == change) {
            askForPokeballConfirmation(pokemon, (Player) player, itemStack, choice);
        } else if (EnumChange.GROWTH_CHANGE == change) {
            askForGrowthConfirmation(pokemon, (Player) player, itemStack, choice);
        }
    }

    private ArrayList<Choice> selectPage(Pokemon pokemon, ArrayList<String> natures, int pageIndex, EnumChange change, ItemStackSnapshot itemStackSnapshot) {
        return createChoices(pokemon, natures, pageIndex, change, itemStackSnapshot);
    }

    private void populatePaginationMap(int choicesPerPage) {
        int numberOfPages = (EnumNature.values().length - 1) / choicesPerPage;
        if (((EnumNature.values().length - 1) % choicesPerPage) != 0)
            numberOfPages++;
        for (int i = 1; i <= numberOfPages; i++) {
            paginationMap.get(EnumChange.NATURE_CHANGE).put(i, new int[]{(i * choicesPerPage) - choicesPerPage, i * choicesPerPage});
        }
        numberOfPages = (EnumPokeballs.values().length - 1) / choicesPerPage;
        if (EnumPokeballs.values().length - 1 % choicesPerPage != 0)
            numberOfPages++;
        for (int i = 1; i <= numberOfPages; i++) {
            paginationMap.get(EnumChange.POKEBALL_CHANGE).put(i, new int[]{(i * choicesPerPage) - choicesPerPage, i * choicesPerPage});
        }
        numberOfPages = (EnumGrowth.values().length - 1) / choicesPerPage;
        if (EnumGrowth.values().length - 1 % choicesPerPage != 0)
            numberOfPages++;
        for (int i = 1; i <= numberOfPages; i++) {
            paginationMap.get(EnumChange.GROWTH_CHANGE).put(i, new int[]{(i * choicesPerPage) - choicesPerPage, i * choicesPerPage});
        }
    }

    //Return true if item was consumed.
    private boolean consumeItem(ItemStackSnapshot itemStackSnapshot, Player player) {
        ItemStack itemStack = itemStackSnapshot.createStack();
        PlayerInventory inv = (PlayerInventory) player.getInventory();
        return inv.query(QueryOperationTypes.ITEM_STACK_EXACT.of(itemStack)).poll(1).isPresent();
    }

    private void doneNatureChange(Pokemon pokemon, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        if (consumeItem(itemStackSnapshot, player)) {
            pokemon.setNature(EnumNature.valueOf(choice));
            player.sendMessage(Config.getMessageAsText("success.nature"));
        }
    }

    private void donePokeballChange(Pokemon pokemon, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        if (consumeItem(itemStackSnapshot, player)) {
            pokemon.setCaughtBall(EnumPokeballs.valueOf(choice));
            player.sendMessage(Config.getMessageAsText("success.pokeball"));
        }
    }

    private void doneGrowthChange(Pokemon pokemon, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        if (consumeItem(itemStackSnapshot, player)) {
            pokemon.setGrowth(EnumGrowth.valueOf(choice));
            player.sendMessage(Config.getMessageAsText("success.growth"));
        }
    }

    private void askForNatureConfirmation(Pokemon pokemon, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        Dialogue confirmationDialogue = Dialogue.builder()
                .setName("Confirmação")
                .setText("Você tem certeza que deseja trocar a nature do seu pokémon para " + choice + "?")
                .addChoice(Choice.builder().setText("Sim").setHandle(dialogueChoiceEvent ->
                        doneNatureChange(pokemon, player, itemStackSnapshot, choice)
                ).build())
                .addChoice(Choice.builder().setText("Não").build())
                .build();
        Sponge.getScheduler().createTaskBuilder()
                .name("OpenConfirmation")
                .delay(3, TimeUnit.MILLISECONDS)
                .execute(task ->
                    Dialogue.setPlayerDialogueData((EntityPlayerMP) player, Collections.singletonList(confirmationDialogue), true)
                )
                .submit(UniqueCosmetics.getInstance());
    }

    private void askForPokeballConfirmation(Pokemon pokemon, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        Dialogue confirmationDialogue = Dialogue.builder()
                .setName("Confirmação")
                .setText("Você tem certeza que deseja trocar a pokébola do seu pokémon para " + choice + "?")
                .addChoice(Choice.builder().setText("Sim").setHandle(dialogueChoiceEvent ->
                    donePokeballChange(pokemon, player, itemStackSnapshot, choice)
                ).build())
                .addChoice(Choice.builder().setText("Não").build())
                .build();
        Sponge.getScheduler().createTaskBuilder()
                .name("OpenConfirmation")
                .delay(3, TimeUnit.MILLISECONDS)
                .execute(task ->
                    Dialogue.setPlayerDialogueData((EntityPlayerMP) player, Collections.singletonList(confirmationDialogue), true)
                )
                .submit(UniqueCosmetics.getInstance());
    }

    private void askForGrowthConfirmation(Pokemon pokemon, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        Dialogue confirmationDialogue = Dialogue.builder()
                .setName("Confirmação")
                .setText("Você tem certeza que deseja trocar o tamanho do seu pokémon para " + choice + "?")
                .addChoice(Choice.builder().setText("Sim").setHandle(dialogueChoiceEvent ->
                    doneGrowthChange(pokemon, player, itemStackSnapshot, choice)
                ).build())
                .addChoice(Choice.builder().setText("Não").build())
                .build();
        Sponge.getScheduler().createTaskBuilder()
                .name("OpenConfirmation")
                .delay(3, TimeUnit.MILLISECONDS)
                .execute(task ->
                    Dialogue.setPlayerDialogueData((EntityPlayerMP) player, Collections.singletonList(confirmationDialogue), true)
                )
                .submit(UniqueCosmetics.getInstance());
    }

    public enum EnumChange {

        NATURE_CHANGE(),
        POKEBALL_CHANGE(),
        GROWTH_CHANGE();

        EnumChange() {
        }
    }


}
