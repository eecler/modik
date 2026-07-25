package petrolpark.mc.destroy.chemistry.neoforge.event;

import petrolpark.mc.destroy.chemistry.api.event.IChemistryEvent;
import petrolpark.mc.destroy.chemistry.api.event.IChemistryEventFirer;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/**
 * The NeoForge implementation of {@link IChemistryEventFirer}.
 * {@link IChemistryEvent}s are fired as NeoForge {@link Event}s on the game event bus.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public class NeoForgeChemistryEventFirer implements IChemistryEventFirer {

    @Override
    public <E extends IChemistryEvent> NeoForgeChemistryEvent<E> fire(E chemistryEvent) {
        NeoForgeChemistryEvent<E> neoForgeEvent = new NeoForgeChemistryEvent<E>(chemistryEvent);
        NeoForge.EVENT_BUS.post(neoForgeEvent);
        return neoForgeEvent;
    };

};
