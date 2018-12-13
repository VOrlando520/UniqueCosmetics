package br.com.pixelmonbrasil.uniquecosmetics.dialogues;

import br.com.pixelmonbrasil.uniquecosmetics.Config;
import br.com.pixelmonbrasil.uniquecosmetics.UniqueCosmetics;
import com.pixelmonmod.pixelmon.api.dialogue.Choice;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerNotLoadedException;
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



    HashMap<EnumChange, HashMap<Integer, int[]>> paginationMap;

    public DialogueManager() {
        paginationMap = new HashMap();
        paginationMap.put(EnumChange.NATURE_CHANGE, new HashMap());
        paginationMap.put(EnumChange.POKEBALL_CHANGE, new HashMap());
        populatePaginationMap(Config.getChoicesPerPage());
    }

    public void reloadPaginationMap() {
        paginationMap.clear();
        paginationMap.put(EnumChange.NATURE_CHANGE, new HashMap());
        paginationMap.put(EnumChange.POKEBALL_CHANGE, new HashMap());
        populatePaginationMap(Config.getChoicesPerPage());
    }

    public ArrayList<Choice> createChoices(EntityPixelmon entityPixelmon, ArrayList<String> choicesList, int pageIndex, EnumChange change, ItemStackSnapshot itemStackSnapshot) {
        ArrayList<Choice> list = new ArrayList();
        int[] bounds = paginationMap.get(change).get(pageIndex).clone();
        for (int i = bounds[0]; i < bounds[1]; i++) {
            if (i < choicesList.size()) {
                String choice = choicesList.get(i);
                list.add(Choice.builder()
                        .setText(choice)
                        .setHandle(choice2 -> handleChoice(choice, entityPixelmon, choice2.player, change, itemStackSnapshot))
                        .build());
            }
        }
        if (pageIndex < paginationMap.get(change).size())
            list.add(Choice.builder()
                    .setText("Próxima página")
                    .setHandle(choice -> choice.reply(createDialogue(entityPixelmon, pageIndex + 1, change, itemStackSnapshot)))
                    .build());
        if (pageIndex > 1) {
            list.add(Choice.builder()
                    .setText("Página anterior")
                    .setHandle(choice -> choice.reply(createDialogue(entityPixelmon, pageIndex - 1, change, itemStackSnapshot)))
                    .build());
        }
        return list;
    }

    public Dialogue createDialogue(EntityPixelmon entityPixelmon, int pageIndex, EnumChange change, ItemStackSnapshot itemStack) {
        ArrayList<String> list = new ArrayList();
        switch (change) {
            case NATURE_CHANGE: {
                for (EnumNature nature : EnumNature.values()) {
                    if (entityPixelmon.getNature() != nature)
                        list.add(nature.name());
                }
                return Dialogue.builder()
                        .setName(Config.getMessage("gui.nature.title"))
                        .setText(Config.getMessage("gui.nature.description"))
                        .setChoices(selectPage(entityPixelmon, list, pageIndex, change, itemStack))
                        .build();
            }
            case POKEBALL_CHANGE: {
                for (EnumPokeballs pokeball : EnumPokeballs.values()) {
                    if (entityPixelmon.caughtBall != pokeball)
                        list.add(pokeball.name());
                }
                return Dialogue.builder()
                        .setName(Config.getMessage("gui.pokeball.title"))
                        .setText(Config.getMessage("gui.pokeball.description"))
                        .setChoices(selectPage(entityPixelmon, list, pageIndex, change, itemStack))
                        .build();
            }
            default:
                throw new RuntimeException("Ocorreu um erro ao processar o diálogo de um item " + change.name());
        }
    }

    private void handleChoice(String choice, EntityPixelmon ep, EntityPlayerMP player, EnumChange change, ItemStackSnapshot itemStack) {
        if (EnumChange.NATURE_CHANGE == change) {
            askForNatureConfirmation(ep, (Player)player, itemStack, choice);
        } else if (EnumChange.POKEBALL_CHANGE == change) {
            askForPokeballConfirmation(ep, (Player)player, itemStack, choice);
        }
    }

    private ArrayList<Choice> selectPage(EntityPixelmon entityPixelmon, ArrayList<String> natures, int pageIndex, EnumChange change, ItemStackSnapshot itemStackSnapshot) {
        return createChoices(entityPixelmon, natures, pageIndex, change, itemStackSnapshot);
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
    }

    //Return true if item was consumed.
    private boolean consumeItem(ItemStackSnapshot itemStackSnapshot, Player player) {
        ItemStack itemStack = itemStackSnapshot.createStack();
        PlayerInventory inv = (PlayerInventory) player.getInventory();
        return inv.query(QueryOperationTypes.ITEM_STACK_EXACT.of(itemStack)).poll(1).isPresent();
    }

    private void doneNatureTransformation(EntityPixelmon ep, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        if (consumeItem(itemStackSnapshot, player)) {
            PokemonSpec.from("nature:" + choice).apply(ep);
            player.sendMessage(Config.getMessageAsText("success.nature"));
            try {
                PixelmonStorage.pokeBallManager.refreshPlayerStorage((EntityPlayerMP)player);
            } catch (PlayerNotLoadedException e) {
                e.printStackTrace();
            }
        }
    }

    private void donePokeballTransformation(EntityPixelmon ep, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        if (consumeItem(itemStackSnapshot, player)) {
            PokemonSpec.from("ball:" + choice).apply(ep);
            player.sendMessage(Config.getMessageAsText("success.pokeball"));
            try {
                PixelmonStorage.pokeBallManager.refreshPlayerStorage((EntityPlayerMP)player);
            } catch (PlayerNotLoadedException e) {
                e.printStackTrace();
            }
        }
    }

    private void askForNatureConfirmation(EntityPixelmon ep, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        Dialogue confirmationDialogue = Dialogue.builder()
                .setName("Confirmação de ação")
                .setText("Você tem certeza que deseja trocar a nature do seu pokémon para " + choice + "?")
                .addChoice(Choice.builder().setText("Sim").setHandle(dialogueChoiceEvent ->  {
                    doneNatureTransformation(ep, player, itemStackSnapshot, choice);
                }).build())
                .addChoice(Choice.builder().setText("Não").build())
                .build();
        Sponge.getScheduler().createTaskBuilder()
                .name("OpenConfirmation")
                .delay(3, TimeUnit.MILLISECONDS)
                .execute( task -> {
                    Dialogue.setPlayerDialogueData((EntityPlayerMP)player, Collections.singletonList(confirmationDialogue), true);
                })
                .submit(UniqueCosmetics.getInstance());
    }

    private void askForPokeballConfirmation(EntityPixelmon ep, Player player, ItemStackSnapshot itemStackSnapshot, String choice) {
        Dialogue confirmationDialogue = Dialogue.builder()
                .setName("Confirmação de ação")
                .setText("Você tem certeza que deseja trocar a pokébola do seu pokémon para " + choice + "?")
                .addChoice(Choice.builder().setText("Sim").setHandle(dialogueChoiceEvent ->  {
                    donePokeballTransformation(ep, player, itemStackSnapshot, choice);
                }).build())
                .addChoice(Choice.builder().setText("Não").build())
                .build();
        Sponge.getScheduler().createTaskBuilder()
                .name("OpenConfirmation")
                .delay(3, TimeUnit.MILLISECONDS)
                .execute( task -> {
                    Dialogue.setPlayerDialogueData((EntityPlayerMP)player, Collections.singletonList(confirmationDialogue), true);
                })
                .submit(UniqueCosmetics.getInstance());
    }

    public enum EnumChange {

        NATURE_CHANGE(),
        POKEBALL_CHANGE();

        EnumChange() {}
    }






}
