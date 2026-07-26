# Отложенные файлы

| Файл | Ждёт |
|---|---|
| `core/chemistry/storage/ElementTankBlock.java` | `content/product/periodictable` (`ElementTankFillingRecipe`) |
| `core/chemistry/storage/ElementTankBlockEntity.java` | `content/product/periodictable` + `core/pollution/PollutingBehaviour` |
| `core/chemistry/storage/ElementTankRenderer.java` | вместе с двумя выше |

# Сессия A (`content/processing/`) — что чем заблокировано

Готово и в `src`: `moltenblock`, `treetap`.

- `cooler` — ждёт семейство `PollutionHelper.pollute(Level, BlockPos, FluidStack...)`. В 1.21.1
  `PollutionHelper` его ещё не содержит, а оно тянет `core/chemistry/hazard/ChemistryHazardHelper`
  (сессия B) и `core/fluid/gasparticle/EvaporatingFluidS2CPacket`. Ещё нужен
  `DestroyTags.Fluids.COOLANT` — это мелочь, добавляется на месте.
- `ageing`, `centrifuge`, `distillation` — ждут `core/recipe/SingleFluidRecipe` и весь
  `core/recipe/ingredient/fluid` (9 файлов). **Самый крупный неразобранный узел порта:** на 1.20.1
  они наследовали Create'овский `FluidIngredient`, в 1.21.1 ингредиенты флюидов — это NeoForge
  `FluidIngredient` + реестр `FluidIngredientType` с `MapCodec`/`StreamCodec` на каждый подтип.
  `com.simibubi.create.foundation.fluid.FluidIngredientOld` — только кодек, а не базовый класс,
  наследоваться от него нельзя. Слой надо переписать честно; он же нужен сессии B для чана.
  Сверх этого centrifuge/distillation ждут `core/block/entity/IDirectionalOutputFluidBlockEntity`,
  `IHaveLabGoggleInformation`, `core/pollution/PollutingBehaviour`, `DestroyPotions`.
- `extrusion` — ждёт `core/explosion/mixedexplosive/ExplosiveProperties` (сессия C).
- `glassblowing` — ждёт `ChemistryHazardHelper` (сессия B), `DestroyMessages` (в 1.21.1 это
  `DestroyPackets`) и `client/DestroyItemDisplayContexts`.
- `phytomining` — **API уже починен, правки лежат в `port/wip`** (кодеки блоков для `BushBlock`,
  выпиленный `PlantType`, `ForgeHooks` → `CommonHooks`/`EventHooks`, `BlockSource` как record,
  `forge:` → `c:` теги, рецепт на `AdvancedProcessingRecipe`). Осталась только регистрация:
  13 блоков свёклы, `MAGIC_BEETROOT_SEEDS`, 15 предметов на `core/item/WithSecondaryItem`,
  `DestroyTags.Items.HEFTY_BEETROOTS`, еда и ассеты на 13 культур.
- `discstamping` — ждёт `ItemApplicationRecipe`/`ItemApplicationRecipeParams` (библиотека даёт
  другие параметры, чем ждёт код 1.20.1) и перевод штампа на DataComponents.
- `dynamo` — тянет `DiscElectroplatingRecipe` из `discstamping` и JEI-категорию из `compat/`.
- `trypolithography` — ждёт 5 пакетов через `DestroyMessages` → `DestroyPackets`,
  `DestroyItemAttributeTypes`, `client/DummyBaker`.

# Взято сессией A за пределами своего раздела

- `DestroyBlocks.STAINLESS_STEEL_BLOCK` и `BOROSILICATE_GLASS` — материалы, в которые застывают
  молтен-блоки; они общие, поэтому лежат в `DestroyBlocks`, а не в холдере кластера.
- `DestroyTags.commonBlockTag(String)` (новый) — блочный близнец `commonItemTag`.
- `DestroyServerConfigs`: впервые подключён `DestroyBlocksConfigs` (`blocks`) — он уже был портирован
  целиком, но ни к чему не прицеплен. Все `DestroyConfigs.server().blocks.*` теперь работают.
- `DestroyRecipeTypes.TAPPING`, `DestroyPartials.TREE_TAP_ARM`, запись в `DestroyVisualizers`.

# Общее для всех: три сессии в одном рабочем дереве

`./gradlew compileJava` красный почти всегда — он падает на чужом незаконченном кластере, а не на
твоём, и `runServer` по той же причине сейчас не запускается ни у кого. Проверять свой кластер так:

```
# один раз, init-скрипт печатает настоящий compileClasspath:
# allprojects { tasks.register("printCompileCp") { doLast {
#   new File("cp.txt").text = project.sourceSets.main.compileClasspath.files.join(":") } } }
./gradlew -q -I cp.gradle printCompileCp
javac -proc:none -d out -cp "$(cat cp.txt)" -sourcepath src/main/java <свои файлы>
```

`-sourcepath` дотянет только то, что реально нужно твоим файлам. И **`git add` только своими
путями** — иначе закоммитишь чужой полуфабрикат.

# Взято сессией B за пределами своего раздела

- `core/fluid/GeniusFluidTankBehaviour.java` — база для всех Mixture-танков, нужна и storage, и vat.
- `core/chemistry/storage/MixtureStorageCapabilities.java` (новый) — `initCapabilities` в 1.21.1 нет,
  Item-капабилити регистрируются событием. Кто портирует другие Item-капабилити — вешайтесь на
  `DestroyCapabilities`, а не на `Destroy` напрямую.
- `IMixtureStorageItem.selectVatTank` и `SinglePhaseVatExtraction` **временно вырезаны** — они
  единственная связь storage → vat. Вернуть при переносе кластера `vat` (сессия B, тот же владелец).

# Взято сессией C (explosion / oil) за пределами своего раздела

- `client/DestroyMenuTypes.java` (новый) — создан с одним `CUSTOM_EXPLOSIVE`. Кто портирует
  `content/redstone/programmer`, допишите туда `REDSTONE_PROGRAMMER` (см. версию в `port/wip` как
  образец) — там же нужны ещё непортированные `RedstoneProgrammerMenu`/`RedstoneProgrammerScreen`.
- `client/DestroyGuiTextures.java` — портирован целиком (232 строки, чистый декларативный enum без
  зависимостей от непортированных классов): circuit/keypunch/vat/redstone_programmer/seismograph/
  custom_explosive/blowpipe/ponder/inventory — всё уже на месте, трогать не нужно.
- AT `net.minecraft.world.level.Explosion`: `level/x/y/z/source/radius/damageSource/
  damageCalculator/random/toBlow/hitPlayers/fire/blockInteraction` открыты в
  `accesstransformer.cfg` — общий файл, если добавляете туда что-то ещё, дописывайте в конец.
