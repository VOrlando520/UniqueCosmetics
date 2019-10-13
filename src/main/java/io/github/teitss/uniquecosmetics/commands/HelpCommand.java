package io.github.teitss.uniquecosmetics.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Arrays;

public class HelpCommand {

    private PaginationList helpPagination = PaginationList.builder ()
            .title (TextSerializers.formattingCode ('&'). deserialize ("& 6 [& l & fHelp UniqueCosmetics & 6]"))
            .header (TextSerializers.formattingCode ('&'). deserialize ("& 7Aliases - uniquecosmetics, uc."))
            .padding (Text.of (TextColors.GOLD, "-"))
            .contents (Arrays.asList (
                    TextSerializers.formattingCode ('&'). Deserialize ("& l & f / uc help & r & 7- Displays the help page."),
                    TextSerializers.formattingCode ('&'). Deserialize ("& l & f / uc naturechanger <player> <quantity> & r & 7- Delivers item to player."),
                    TextSerializers.formattingCode ('&'). Deserialize ("& l & f / uc pokeballchanger <player> <quantity> & r & 7- Delivers item to player."),
                    TextSerializers.formattingCode ('&'). Deserialize ("& l & f / uc shinytransformation <player> <quantity> & r & 7- Delivers the item to the player."),
                    TextSerializers.formattingCode ('&'). Deserialize ("& l & f / uc growthtransformation <player> <quantity> & r & 7- Delivers item to player."),
                    TextSerializers.formattingCode ('&'). Deserialize ("& l & fTo reload the configuration use the Sponge command.")))
            .build ();

    private CommandSpec commandSpec = CommandSpec.builder ()
            .permission ("uniquecosmetics.command.help")
            .description (Text.of ("Unique Cosmetics Plugin Help Command."))
            .executor (new CommandExecutor () {
                @Override
                public CommandResult execute (CommandSource src, CommandContext args) throws CommandException {
                    helpPagination.sendTo (src);
                    return CommandResult.success ();
                }
            })
            .build ();

    public CommandSpec getCommandSpec () {
        return commandSpec;
    }

}
