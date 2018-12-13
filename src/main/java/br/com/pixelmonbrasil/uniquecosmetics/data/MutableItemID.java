package br.com.pixelmonbrasil.uniquecosmetics.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class MutableItemID extends AbstractData<MutableItemID, ImmutableItemID> {

    private String itemID;

    public MutableItemID() {
        this("");
    }

    public MutableItemID(String itemID) {
        this.itemID = itemID;
        registerGettersAndSetters();
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public Value<String> itemID() {
        return Sponge.getRegistry().getValueFactory().createValue(UCKeys.ITEMID, "");
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(UCKeys.ITEMID, this::getItemID);
        registerFieldSetter(UCKeys.ITEMID, this::setItemID);
        registerKeyValue(UCKeys.ITEMID, this::itemID);
    }

    @Override
    public Optional<MutableItemID> fill(DataHolder dataHolder, MergeFunction overlap) {
        dataHolder.get(MutableItemID.class).ifPresent(data -> {
            this.itemID = overlap.merge(this, data).itemID;
        });
        return Optional.of(this);
    }

    @Override
    public Optional<MutableItemID> from(DataContainer container) {
        if(!container.contains(DataQuery.of("UniqueCosmeticsItemID")))
            return Optional.empty();
        itemID = container.getString(DataQuery.of("UniqueCosmeticsItemID")).get();
        return Optional.of(this);
    }

    @Override
    public MutableItemID copy() {
        return new MutableItemID(itemID);
    }

    @Override
    public ImmutableItemID asImmutable() {
        return new ImmutableItemID(itemID);
    }

    @Override
    public int getContentVersion() {
        return ItemIDBuilder.CONTAINER_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = super.toContainer();
        if (itemID != null) {
            container.set(UCKeys.ITEMID, itemID);
        }
        return container;
    }
}
