package br.com.pixelmonbrasil.uniquecosmetics.commands;

import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class BaseCommand {

    private CommandSpec commandSpec = CommandSpec.builder()
            .description(Text.of("Comando base"))
            .child(new GiveNatureCommand().getCommandSpec(), "naturechanger")
            .child(new GivePokeballCommand().getCommandSpec(), "pokeballchanger")
            .child(new GiveShinyCommand().getCommandSpec(), "shinytransformation")
            .child(new HelpCommand().getCommandSpec(), "help")
            .build();

    public CommandSpec getCommandSpec() {
        return commandSpec;
    }
}
