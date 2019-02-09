package io.github.teitss.uniquecosmetics.data;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class ItemIDBuilder extends AbstractDataBuilder<MutableItemID> implements DataManipulatorBuilder<MutableItemID, ImmutableItemID> {

    public static int CONTAINER_VERSION = 1;

    public ItemIDBuilder() {
        super(MutableItemID.class, CONTAINER_VERSION);
    }

    @Override
    protected Optional<MutableItemID> buildContent(DataView container) throws InvalidDataException {
        if (!container.contains(DataQuery.of("UniqueCosmeticsItemID"))) {
            return Optional.empty();
        }
        return Optional.of(new MutableItemID(container.getString(DataQuery.of("UniqueCosmeticsItemID")).get()));
    }

    @Override
    public MutableItemID create() {
        return new MutableItemID();
    }

    @Override
    public Optional<MutableItemID> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }
}
