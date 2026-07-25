# Chemistry files waiting on content

Three chemistry files taken off the build. Everything they need is Destroy *content* that has
not been ported to 1.21.1 yet, not chemistry — the rest of the engine (196 files) compiles.

Nothing on the build references them, so putting them back is just a `git mv` away once their
dependencies exist.

## `DestroyReactions.java` (49 errors)

The reaction index — the definitions of every Destroy reaction. Needs the content registries:

- `DestroyItems`, `DestroyBlocks` — reactants and products are specific items and blocks
- `DestroySubstancesConfigs` — per-substance tuning values
- `ExplosionReactionResult` — parked below

This is data about content, so it can only come back with the item/block registries.

## `MixtureFluid.java` (10 errors)

Needs `DestroyFluids`, and is where the deepest 1.21.1 break lives: all six removed-NBT calls
(`getOrCreateTag`, `getOrCreateChildTag`, `getChildTag`, `removeChildTag`) are in this one
file. 1.20.5 replaced `FluidStack`/`ItemStack` NBT with data components, so the Mixture
payload needs a `DataComponentType` with a `Codec`/`StreamCodec` instead of a `"Mixture"`
child tag. `DestroyAttachmentTypes` and `DestroyDataMapTypes` on this branch are the pattern
to copy.

Its only referrer is `PollutionPonderScenes`, which is entirely commented out.

## `ExplosionReactionResult.java` (7 errors)

Needs `SmartExplosion` (413 lines), which is a hard port in its own right, not a mechanical one:

- it uses `ProtectionEnchantment`, **removed in 1.21** — enchantments became data-driven
- `ClientboundExplodePacket`'s constructor changed
- `ForgeEventFactory` is `EventHooks` on NeoForge
- and it pulls in three more unported classes: `DestroyDamageSources`,
  `DestroyLootContextParams`, `CustomExplosiveMixExplosion`

Its only referrer is `DestroyReactions`, parked above, so the two come back together.

## What did get solved rather than parked

For contrast, these were resolved instead of deferred, because the engine core depends on them:

- `ReactionResult.onVatReaction` no longer takes `VatControllerBlockEntity` (887 lines of
  content). It takes `IVatReactionContext`, an interface in the chemistry package declaring
  the three things Reaction Results actually used. The Vat implements it when it is ported.
- `PlayerNovelCompoundsSynthesizedCapability` became `PlayerNovelCompoundsSynthesized`, a
  NeoForge data attachment (capabilities on entities are gone).
- `DestroyAdvancementTrigger` could not be seamed around — callers name individual enum
  constants — so it was ported onto vanilla `PlayerTrigger` + Registrate + `AdvancementHolder`.
