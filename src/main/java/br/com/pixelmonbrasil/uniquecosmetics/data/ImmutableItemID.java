package br.com.pixelmonbrasil.uniquecosmetics.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableItemID extends AbstractImmutableData<ImmutableItemID, MutableItemID> {

    private final String itemID;
    private final ImmutableValue<String> immutableItemID;

    public ImmutableItemID(String itemID) {
        registerGetters();
        this.itemID = itemID;
        this.immutableItemID = Sponge.getRegistry().getValueFactory().createValue(UCKeys.ITEMID, this.itemID)
                .asImmutable();
    }

    public ImmutableValue<String> getImmutableItemID() {
        return immutableItemID;
    }

    public String getItemID() {
        return itemID;
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(UCKeys.ITEMID, this::getItemID);
        registerKeyValue(UCKeys.ITEMID, this::getImmutableItemID);
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

    @Override
    public MutableItemID asMutable() {
        return new MutableItemID(itemID);
    }
}
