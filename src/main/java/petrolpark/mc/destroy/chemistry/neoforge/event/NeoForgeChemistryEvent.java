package petrolpark.mc.destroy.chemistry.neoforge.event;

import petrolpark.mc.destroy.chemistry.api.event.IChemistryEvent;
import petrolpark.mc.destroy.chemistry.api.event.IFiredChemistryEvent;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * The NeoForge implementation of {@link IFiredChemistryEvent}.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public class NeoForgeChemistryEvent<E extends IChemistryEvent> extends Event implements IFiredChemistryEvent<E>, ICancellableEvent {

    protected final E wrapped;

    public NeoForgeChemistryEvent(E chemistryEvent) {
        wrapped = chemistryEvent;
    };

    @Override
    public E get() {
        return wrapped;
    };

    /**
     * Whether cancelling this Event has any effect on the routine which fired the wrapped
     * {@link IChemistryEvent}.
     * <p>
     * NeoForge decides cancellability statically, by whether an Event implements
     * {@link ICancellableEvent} - unlike Forge, which asked each instance via
     * {@code isCancelable()}. This class therefore always implements
     * {@link ICancellableEvent}, so a subscriber can always call
     * {@link ICancellableEvent#setCanceled(boolean)} and always stop the Event reaching
     * further subscribers. This method reports whether the firing routine will actually
     * act on that, which is what {@link IChemistryEvent#isCancellable()} has always meant.
     * </p>
     */
    public boolean isCancellable() {
        return wrapped.isCancellable();
    };

    @Override
    public boolean isCancelled() {
        return isCanceled();
    };

};
