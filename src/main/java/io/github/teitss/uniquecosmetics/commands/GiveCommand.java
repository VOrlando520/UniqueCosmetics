package io.github.teitss.uniquecosmetics.commands;

import io.github.teitss.uniquecosmetics.Config;
import io.github.teitss.uniquecosmetics.UniqueCosmetics;
import io.github.teitss.uniquecosmetics.data.MutableItemID;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
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
import java.util.stream.Collectors;

public class GiveCommand {

    private CommandSpec commandSpec = CommandSpec.builder()
            .description(Text.of("Gives cosmetics to players."))
            .permission("uniquecosmetics.command.give")
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                    GenericArguments.choicesInsensitive(Text.of("cosmetic"),
                            UniqueCosmetics.COSMETIC_REGISTRY.keySet().stream()
                                    .collect(Collectors.toMap(key -> key, key -> key))),
                    GenericArguments.onlyOne(GenericArguments.integer(Text.of("quantity"))))
            .executor((src, args) -> {
                if (src instanceof Player || src instanceof ConsoleSource) {
                    Player player = args.<Player>getOne("player").get();
                    String cosmetic = args.<String>getOne(Text.of("cosmetic")).get();
                    int quantity = args.<Integer>getOne("quantity").get();
                    if (quantity > 64 || quantity <= 0) {
                        src.sendMessage(Text.of(TextColors.RED, "Invalid quantity, please choose a number between 1 and 64"));
                        return CommandResult.success();
                    }
                    ItemStack itemStack = Config.getItemsMap().get(cosmetic).copy();
                    itemStack.setQuantity(quantity);
                    itemStack.offer(new MutableItemID(cosmetic));
                    InventoryTransactionResult itr = ((PlayerInventory) player.getInventory()).getMain().offer(itemStack);
                    src.sendMessage(Text.of(TextColors.GREEN, "Were delivered " + quantity + " items for " + player.getName() + "."));
                    ArrayList<ItemStackSnapshot> rejectedItems = new ArrayList<>(itr.getRejectedItems());
                    if (!rejectedItems.isEmpty()) {
                        Item item = (Item) player.getWorld().createEntity(EntityTypes.ITEM, player.getLocation().getPosition());
                        item.offer(Keys.REPRESENTED_ITEM, rejectedItems.get(0));
                        player.sendTitle(Title.builder()
                                .title(Text.of(TextColors.RED, "NOTICE!"))
                                .subtitle(Text.of("Inventory full, item have fallen to the ground around you"))
                                .build());
                        player.getWorld().spawnEntity(item);
                    }
                }
                return CommandResult.success();
            })
            .build();

    public CommandSpec getCommandSpec() {
        return commandSpec;
    }

}
