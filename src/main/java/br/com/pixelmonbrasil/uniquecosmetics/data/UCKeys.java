package br.com.pixelmonbrasil.uniquecosmetics.data;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

public class UCKeys {

    public static Key<Value<String>> ITEMID = Key.builder()
            .type(new TypeToken<Value<String>>() {})
            .id("uc_itemid")
            .name("UniqueCosmeticsItemID")
            .query(DataQuery.of("UniqueCosmeticsItemID"))
            .build();

}
