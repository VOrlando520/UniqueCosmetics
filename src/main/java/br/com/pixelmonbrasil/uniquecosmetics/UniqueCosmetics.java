package br.com.pixelmonbrasil.uniquecosmetics;

import br.com.pixelmonbrasil.uniquecosmetics.commands.BaseCommand;
import br.com.pixelmonbrasil.uniquecosmetics.data.ImmutableItemID;
import br.com.pixelmonbrasil.uniquecosmetics.data.ItemIDBuilder;
import br.com.pixelmonbrasil.uniquecosmetics.data.MutableItemID;
import br.com.pixelmonbrasil.uniquecosmetics.data.UCKeys;
import br.com.pixelmonbrasil.uniquecosmetics.dialogues.DialogueManager;
import br.com.pixelmonbrasil.uniquecosmetics.listeners.InteractEntityListener;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;

@Plugin(
        id="uniquecosmetics",
        name="Unique Cosmetics",
        version="1.1.0",
        authors="Teits / Discord Teits#7663",
        description="Plugin de cosméticos, que mundo superficial!",
        dependencies=@Dependency(id="pixelmon")
)
public class UniqueCosmetics {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot=false)
    private Path path;

    @Inject
    private PluginContainer container;

    private static UniqueCosmetics instance;
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private DialogueManager dialogueManager;

    @Listener
    public void onKeyRegistration(GameRegistryEvent.Register<Key<?>> event) {
        UCKeys.ITEMID = Key.builder()
                .type(new TypeToken<Value<String>>() {})
                .id("uc_itemid")
                .name("UniqueCosmeticsItemID")
                .query(DataQuery.of("UniqueCosmeticsItemID"))
                .build();
    }

    @Listener
    public void onDataRegistration(GameRegistryEvent.Register<DataRegistration<?, ?>> event) {
        DataRegistration.builder()
                .dataClass(MutableItemID.class)
                .immutableClass(ImmutableItemID.class)
                .builder(new ItemIDBuilder())
                .manipulatorId("uc_itemid")
                .dataName("UniqueCosmeticsItemID")
                .buildAndRegister(container);
    }

    @Listener
    public void onGameInit(GameInitializationEvent e) {
        instance = this;
        logger.info("Carregando configurações...");
        configLoader = HoconConfigurationLoader.builder().setPath(path.resolve("uniquecosmetics.conf")).build();
        Config.install(path, configLoader);
        dialogueManager = new DialogueManager();
        logger.info("Registrando eventos...");
        Sponge.getEventManager().registerListeners(this, new InteractEntityListener());
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent e) {
        logger.info("Registrando comandos...");
        Sponge.getCommandManager().register(this, new BaseCommand().getCommandSpec(), "uniquecosmetics", "uc");
    }

    @Listener
    public void onGameReload(GameReloadEvent e) {
        Config.load(configLoader);
        dialogueManager.reloadPaginationMap();
    }

    public static UniqueCosmetics getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }
}
