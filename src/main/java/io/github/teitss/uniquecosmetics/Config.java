package io.github.teitss.uniquecosmetics;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.codehaus.plexus.util.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private static HashMap<String, String> messagesMap = new HashMap<>();
    private static HashMap<String, ItemStack> itemsMap = new HashMap<>();
    private static int choicesPerPage;

    public static void install(Path path, ConfigurationLoader<CommentedConfigurationNode> configManager) {
        if (Files.notExists(path.resolve("uniquecosmetics.conf")))
            setup(path);
        load(configManager);

    }

    public static void load(ConfigurationLoader<CommentedConfigurationNode> configManager) {
        try {
            ConfigurationNode configNode = configManager.load();
            messagesMap.clear();
            for(Map.Entry<Object, ? extends ConfigurationNode> entry : configNode.getNode("messages").getChildrenMap().entrySet()) {
                messagesMap.put((String) entry.getKey(), entry.getValue().getString());
            }
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : configNode.getNode("items").getChildrenMap().entrySet()) {
                itemsMap.put((String) entry.getKey(), entry.getValue().getValue(TypeToken.of(ItemStack.class)));
            }
            choicesPerPage = configNode.getNode("gui", "choicesPerPage").getInt();
            UniqueCosmetics.INSTANCE.getLogger().info("Config loaded with success.");
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public static void setup(Path path) {
        try {
            Asset configFile = Sponge.getAssetManager().getAsset(UniqueCosmetics.INSTANCE, "uniquecosmetics.conf").get();
            configFile.copyToDirectory(path);
            UniqueCosmetics.INSTANCE.getLogger().info("Config installed with success.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Text getMessageAsText(String id) {
        return TextSerializers.FORMATTING_CODE.deserialize(messagesMap.get(id));
    }

    public static Text getMessageAsTextWithPlaceholders(String id, String regex, String value) {
        return TextSerializers.FORMATTING_CODE.deserialize(StringUtils.replace(messagesMap.get(id), regex, value));
    }

    public static String getMessageWithPlaceholders(String id, String regex, String value) {
        return StringUtils.replace(messagesMap.get(id), regex, value);
    }

    public static String getMessage(String id) {
        return messagesMap.get(id);
    }

    public static int getChoicesPerPage() {
        return choicesPerPage;
    }

    public static HashMap<String, ItemStack> getItemsMap() {
        return itemsMap;
    }
}
