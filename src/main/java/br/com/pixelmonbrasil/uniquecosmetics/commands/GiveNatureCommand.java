package br.com.pixelmonbrasil.uniquecosmetics.commands;

import br.com.pixelmonbrasil.uniquecosmetics.Config;
import br.com.pixelmonbrasil.uniquecosmetics.data.MutableItemID;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

import java.util.ArrayList;

public class GiveNatureCommand {

    private CommandSpec commandSpec = CommandSpec.builder()
            .description(Text.of("Comando para entregar itens de mudança de nature."))
            .permission("uniquecosmetics.command.give.nature")
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                    GenericArguments.onlyOne(GenericArguments.integer(Text.of("quantity"))))
            .executor(new CommandExecutor() {
                @Override
                public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                    if(src instanceof Player || src instanceof ConsoleSource) {
                        Player player = args.<Player>getOne("player").get();
                        int quantity = args.<Integer>getOne("quantity").get();
                        if(quantity > 64 || quantity <= 0) {
                            src.sendMessage(Text.of(TextColors.RED, "Quantidade inválida, por favor use um número entre 1 e 64"));
                            return CommandResult.success();
                        }
                        ItemStack itemStack = Config.getNatureChangerItemStack().copy();
                        itemStack.setQuantity(quantity);
                        itemStack.offer(new MutableItemID("natureChanger"));
                        InventoryTransactionResult itr = ((PlayerInventory) player.getInventory()).getMain().offer(itemStack);
                        src.sendMessage(Text.of(TextColors.GREEN, "Foram entregues " + quantity + " itens para " + player.getName() + "."));
                        ArrayList<ItemStackSnapshot> rejectedItems = new ArrayList(itr.getRejectedItems());
                        if (!rejectedItems.isEmpty()) {
                            Item item = (Item) player.getWorld().createEntity(EntityTypes.ITEM, player.getLocation().getPosition());
                            item.offer(Keys.REPRESENTED_ITEM, rejectedItems.get(0));
                            player.sendTitle(Title.builder()
                                    .title(Text.of(TextColors.RED, "AVISO!"))
                                    .subtitle(Text.of("Inventário cheio, o item caiu no chão ao seu redor."))
                                    .build());
                            player.getWorld().spawnEntity(item);
                        }
                    }
                    return CommandResult.success();
                }
            })
            .build();

    public CommandSpec getCommandSpec() {
        return commandSpec;
    }
}
