package br.com.pixelmonbrasil.uniquecosmetics.commands;

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

    private PaginationList helpPagination = PaginationList.builder()
            .title(TextSerializers.formattingCode('&').deserialize("&6[&l&fAjuda UniqueCosmetics&6]"))
            .header(TextSerializers.formattingCode('&').deserialize("&7Aliases - uniquecosmetics, uc."))
            .padding(Text.of(TextColors.GOLD, "-"))
            .contents(Arrays.asList(
                    TextSerializers.formattingCode('&').deserialize("&l&f/uc help &r&7- Exibe a página de ajuda."),
                    TextSerializers.formattingCode('&').deserialize("&l&f/uc naturechanger <jogador> <quantidade> &r&7- Entrega o item para o jogador."),
                    TextSerializers.formattingCode('&').deserialize("&l&f/uc pokeballchanger <jogador> <quantidade> &r&7- Entrega o item para o jogador."),
                    TextSerializers.formattingCode('&').deserialize("&l&f/uc shinytransformation <jogador> <quantidade> &r&7- Entrega o item para o jogador."),
                    TextSerializers.formattingCode('&').deserialize("&l&fPara recarregar a configuração use o comando do Sponge.")))
            .build();

    private CommandSpec commandSpec = CommandSpec.builder()
            .permission("uniquecosmetics.command.help")
            .description(Text.of("Comando de ajuda do plugin Unique Cosmetics."))
            .executor(new CommandExecutor() {
                @Override
                public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                    helpPagination.sendTo(src);
                    return CommandResult.success();
                }
            })
            .build();

    public CommandSpec getCommandSpec() {
        return commandSpec;
    }

}
