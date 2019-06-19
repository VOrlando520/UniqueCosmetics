package io.github.teitss.uniquecosmetics;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.teitss.uniquecosmetics.commands.BaseCommand;
import io.github.teitss.uniquecosmetics.data.ImmutableItemID;
import io.github.teitss.uniquecosmetics.data.ItemIDBuilder;
import io.github.teitss.uniquecosmetics.data.MutableItemID;
import io.github.teitss.uniquecosmetics.data.UCKeys;
import io.github.teitss.uniquecosmetics.items.CosmeticItem;
import io.github.teitss.uniquecosmetics.items.NatureChanger;
import io.github.teitss.uniquecosmetics.items.PokeballChanger;
import io.github.teitss.uniquecosmetics.items.ShinyChanger;
import io.github.teitss.uniquecosmetics.listeners.InteractEntityListener;
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
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.HashMap;

@Plugin(
        id="uniquecosmetics",
        name="Unique Cosmetics",
        version = "@pluginVersion@",
        authors="Teits / Discord Teits#7663",
        description="Plugin de cosméticos, que mundo superficial!",
        dependencies = @Dependency(id = "pixelmon")
)
public class UniqueCosmetics {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot=false)
    private Path path;

    @Inject
    private PluginContainer container;

    public static UniqueCosmetics INSTANCE;
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    public static HashMap<String, CosmeticItem> COSMETIC_REGISTRY;

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
        INSTANCE = this;
        logger.info("Reading configuration file...");
        configLoader = HoconConfigurationLoader.builder().setPath(path.resolve("uniquecosmetics.conf")).build();
        Config.install(path, configLoader);
        logger.info("Registering cosmetics...");
        COSMETIC_REGISTRY = new HashMap<>();
        registerCosmetics();
        logger.info("Registering event listeners...");
        Sponge.getEventManager().registerListeners(this, new InteractEntityListener());
        logger.info("Registering commands...");
        Sponge.getCommandManager().register(this, new BaseCommand().getCommandSpec(), "uniquecosmetics", "uc");
    }

    @Listener
    public void onGameReload(GameReloadEvent e) {
        Config.load(configLoader);
        registerCosmetics();
    }

    private void registerCosmetics() {
        COSMETIC_REGISTRY.put("natureChanger", new NatureChanger());
        COSMETIC_REGISTRY.put("pokeballChanger", new PokeballChanger());
        COSMETIC_REGISTRY.put("shinyTransformation", new ShinyChanger());
    }

    public Logger getLogger() {
        return logger;
    }
}
