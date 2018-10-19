package br.com.pixelmonbrasil.uniquecosmetics;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
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

    private static HashMap<String, String> messagesMap = new HashMap();
    private static int choicesPerPage;
    private static ItemStack natureChangerItemStack;
    private static ItemStack pokeballChangerItemStack;
    private static ItemStack shinyTransformationItemStack;

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
            choicesPerPage = configNode.getNode("gui", "choicesPerPage").getInt();
            natureChangerItemStack = configNode.getNode("items", "natureChanger").getValue(TypeToken.of(ItemStack.class));
            pokeballChangerItemStack = configNode.getNode("items", "pokeballChanger").getValue(TypeToken.of(ItemStack.class));
            shinyTransformationItemStack = configNode.getNode("items", "shinyTransformation").getValue(TypeToken.of(ItemStack.class));
            UniqueCosmetics.getInstance().getLogger().info("Configuração carregada com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public static void setup(Path path) {
        try {
            Asset configFile = Sponge.getAssetManager().getAsset(UniqueCosmetics.getInstance(), "uniquecosmetics.conf").get();
            configFile.copyToDirectory(path);
            UniqueCosmetics.getInstance().getLogger().info("Configurações instaladas com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Text getMessageAsText(String id) {
        return TextSerializers.FORMATTING_CODE.deserialize(messagesMap.get(id));
    }

    public static String getMessage(String id) {
        return messagesMap.get(id);
    }

    public static int getChoicesPerPage() {
        return choicesPerPage;
    }

    public static ItemStack getNatureChangerItemStack() {
        return natureChangerItemStack;
    }

    public static ItemStack getPokeballChangerItemStack() {
        return pokeballChangerItemStack;
    }

    public static ItemStack getShinyTransformationItemStack() {
        return shinyTransformationItemStack;
    }
}
