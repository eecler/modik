# Port quarantine

Code moved out of `src/main/java` so it does not participate in compilation, kept here
rather than deleted so nothing is lost while the 1.21.1 NeoForge port is in progress.

## `seismology/` (9 files)

Taken off the build during the initial 1.21.1 NeoForge setup. These files came with
upstream's `1.21.1-neo` branch but cannot compile against anything publicly available:

1. **Their `package` declaration does not match their directory.** Every file declares
   `package petrolpark.mc.library.destroy.content.oil.seismology`, while living under
   `petrolpark/mc/destroy/core/seismology`. Upstream moved this code into Petrolpark
   Library and left stale copies behind in Destroy.

2. **They still import Forge, not NeoForge** — `net.minecraftforge.common.util.LazyOptional`
   (`SeismometerItem`) and `net.minecraftforge.network.NetworkEvent`
   (`SeismometerSpikeS2CPacket`). Both types no longer exist: NeoForge replaced
   capabilities/`LazyOptional` and moved networking to the payload system.

3. **They reference library packages that the published library does not contain.**
   `petrolpark.mc.library.network.packet.{C2SPacket,S2CPacket}`,
   `petrolpark.mc.library.destroy.{DestroyItems,DestroyMessages,DestroyAdvancementTrigger}`,
   `petrolpark.mc.library.destroy.client.DestroyLang` and
   `petrolpark.mc.library.destroy.content.oil.ChunkCrudeOil` are all absent from
   Petrolpark Library `1.21.1-1.5.0` on Modrinth — upstream builds against a newer
   local (unpublished) library, so these only resolve on their machine.

Together these accounted for most of the 190 compile errors in the initial build.

### Restoring it

Seismograph/seismometer content is unavailable until either Petrolpark Library publishes
the `library.destroy.*` and `library.network.packet` packages, or the code is reworked to
be self-contained inside Destroy (port `LazyOptional` to NeoForge capabilities and
`NetworkEvent` to `CustomPacketPayload` + `StreamCodec`).

This is *not* a priority for the current milestone (chemistry engine) and is unrelated to
the pollution system, which stayed in the build.
