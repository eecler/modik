# Destroy 1.20.1 → 1.21.1 NeoForge — состояние порта и правила работы

**Читай этот файл первым. Он заменяет разведку по репозиторию — не переоткрывай то, что здесь написано.**

Цель: весь мод работает на 1.21.1 NeoForge (Create 6.0.10, Flywheel 1.0.6, Petrolpark Library 1.21.1-1.5.0).

---

## 1. Где мы

| | Файлов |
|---|---|
| Работает в `src/main/java` (компилируется, сервер грузится) | **375** |
| Осталось в `port/wip` | **377** |
| Отложено осознанно: `compat/` (59) + `mixin/` (42) | 101 |

Собирается: `./gradlew build` → BUILD SUCCESSFUL. Сервер: `./gradlew runServer` → `Done (~1s)`.
Ветка `1.21.1-neo`. Последний коммит порта — «Port the Mechanical Sieve, mob effects and their registries».

**Что уже живое:** химический движок, реакции, предметы/флюиды, конфиги, загрязнение, механическое сито
(первая машина целиком), 11 мод-эффектов с атрибутами и уроном.

**Архитектура порта:** база — скелет `upstream/1.21.1-neo` (пакет `petrolpark.mc.destroy`,
Registrate от библиотеки), а основной объём кода переносится из ветки `1.20.1` (пакет
`com.petrolpark.destroy`). Истории веток не связаны — это не merge, а перенос.

---

## 2. Правила работы (нарушение = потерянное время)

1. **Коммить на каждом зелёном.** Не в конце сессии. В прошлой сессии потеряли полчаса работы.
2. **Никогда `git checkout -- <каталог>`** при незакоммиченных правках. Он сносит всё молча.
3. **Не измеряй — компилируй.** Не трать токены на обследования и подсчёты ошибок.
   Ставь кластер целиком → `./gradlew compileJava` → чини верхнюю ошибку → повтори.
   Компилятор быстрее любой аналитики.
4. **Не удаляй каталоги в `src` через `rm -rf`** — сначала проверь `git ls-files`, там могут быть
   отслеживаемые файлы из скелета.
5. **Не трогай чужой кластер.** Каталоги распределены (раздел 5).
6. **`compat/` и `mixin/` не переносим.** Они вне source set; миксины гейтятся `destroy.mixins.json`.
   Исключение — аксессор-миксины (см. explosion в разделе 5).
7. Java 21: `JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home`.
8. В сообщениях коммитов **не упоминать Claude / Co-Authored-By**.

---

## 3. Единый паттерн 1.21.1 — прочитай, сэкономит часы

Почти всё, что ломается, — это **одна миграция**: сериализация стала registry-aware.
Теги больше не самодостаточны, внутри лежат ссылки на реестровые объекты, поэтому:

- `read/write(CompoundTag, boolean)` → `+ HolderLookup.Provider registries` (и в `super.` тоже)
- `ItemStackHandler.serializeNBT()` / `deserializeNBT(tag)` → требуют `Provider`
- ItemStack NBT → **DataComponents** (`DataComponents.CUSTOM_DATA`)
- Капабилити → **Data Attachments** (кодек вместо `serializeNBT`)
- Пакеты → `CustomPacketPayload` + `StreamCodec`
- Рецепты → `MapCodec` + `StreamCodec`
- Статические инстансы → `Holder<T>` (эффекты, атрибуты, типы загрязнения)

Прочие частые ломы:
- `new ResourceLocation(..)` → `ResourceLocation.parse` / `fromNamespaceAndPath`
- `MobEffect.applyEffectTick` возвращает `boolean`; `isDurationEffectTick` → `shouldApplyEffectTickThisTick`;
  `getCurativeItems()` → `fillEffectCures(Set<EffectCure>, MobEffectInstance)`
- Модификаторы атрибутов — по `ResourceLocation`, не по UUID-строкам; `Operation.ADDITION` → `ADD_VALUE`,
  `MULTIPLY_TOTAL` → `ADD_MULTIPLIED_TOTAL`
- `setSecondsOnFire(int)` → `igniteForSeconds(float)`; `BucketPickup.pickupBlock` получил `Player`
- `stack.isEdible()` / `getFoodProperties()` → `stack.get(DataComponents.FOOD)`
- Flywheel-визуалы регистрируются напрямую (`SimpleBlockEntityVisualizer.builder(...)`), потому что
  Registrate здесь библиотечный и у него нет `.visual()` на билдере block entity —
  см. `client/DestroyVisualizers.java`
- Конфиги: `DestroyAllConfigs.SERVER.x` → `DestroyConfigs.server().x`

---

## 4. Инструменты (уже написаны, используй, не переписывай)

- **`port/tools/legacy_rewrite.py`** — пакетный автозаменщик по `port/wip`. Идемпотентный.
  Dry-run по умолчанию, `--apply` пишет изменения + `flags.tsv`.
  Уже применён (~380 замен). Запускай после того, как добавишь новое правило.
- **`port/tools/convert_packets.py`** — генератор пакетов 1.20.1 → Catnip payload.
  13 из 25 конвертирует механически, 7 отбрасывает как дубликаты, 5 честно отказывается.
- **`port/tools/quarantine.py`** — глушит листовые методы сериализации по логу ошибок.
  **Применять осторожно:** закарантиненный block entity не сохраняет состояние. Только как
  промежуточный шаг, никогда в релиз. Долг пишется в `quarantined.tsv`.
- **Шимы `src/main/java/petrolpark/mc/destroy/legacy/`**:
  - `LegacyNBT` — старый NBT-API поверх DataComponents. **Возвращает копии!** Если старый код
    мутировал живой тег — переводи на `LegacyNBT.update()`.
  - `LegacyRegistries` — ambient `HolderLookup.Provider`. Только при загруженном мире,
    не в бутстрапе/датагене/кодеках рецептов.
  - `LegacyCapabilities`, `LegacyRecipes` — лукапы и разворачивание `RecipeHolder`.
- **`port/tools/flags.tsv`** — карта мест, которые скрипт намеренно не трогает (559 строк).

---

## 5. Распределение работы на 3 сессии

Каталоги не пересекаются — параллельные сессии не конфликтуют, **кроме реестров** (см. ниже).

### Сессия A — обработка (`content/processing/`)
Начни с `moltenblock` (7 файлов, ~10 проблем — самый быстрый старт в проекте), затем
`trypolithography` (24 файла, ~54 проблемы), `glassblowing`, `distillation`, `centrifuge`,
`extrusion`, `treetap`, `dynamo`.
`moltenblock` конкретно: три переименования API (`igniteForSeconds`, `pickupBlock` + `Player`,
frost walker) + регистрация 6 блоков/флюидов/вёдер с ассетами.

### Сессия B — химия (`core/chemistry/`)
`storage` (24 файла, ~66 проблем), `vat` (29 файлов, ~83 проблемы — самый тяжёлый в моде,
бери последним), `hazard`.
Здесь же основная масса капабилити → Data Attachments.

### Сессия C — взрывы и нефть (`core/explosion/`, `content/oil/`)
**Первым делом — аксессор-миксин к `Explosion`.** 60 из 548 ошибок кластера `explosion` — это
приватные `level`, `radius`, `toBlow`. Один accessor-миксин (плагин миксинов в проекте уже есть)
открывает 41 файл. Самая высокая отдача на единицу усилия во всём моде.
Дальше `explosion` (41), `mixedexplosive` (16), `oil/seismology` (8), `oil/pumpjack` (8).

### Инвентарь остатка по каталогам (на момент написания, 378 файлов)

```
  25  (root — DestroyTrades, DestroyVillagers, DestroyLoot, DestroyDisplaySources и пр.)
  22  core/explosion/mixedexplosive        9  content/oil/pumpjack
  19  core/explosion                       9  content/processing/glassblowing
  15  client                               9  content/processing/trypolithography/keypunch
  14  core/chemistry/storage               9  core/recipe/ingredient/fluid
  13  content/redstone/programmer          8  content/oil/seismology
  11  content/processing/trypolithography  8  content/product/periodictable
  11  core/chemistry/vat                   7  content/processing/dynamo
   9  content/product                      7  content/processing/moltenblock
                                           7  core/pollution
```

Каталог `(root)` и `client/` — общие, их берёт та сессия, которой они понадобились первой;
предупреди остальных в `port/tools/blocked.md`.

### Общий узел: реестры
`DestroyBlocks`, `DestroyItems`, `DestroyBlockEntityTypes`, `DestroyPackets` нужны всем.
**Правило:** каждая сессия заводит свой holder-файл (`VatBlocks.java`, `ExplosionBlocks.java`…)
и добавляет **одну строку** вызова в общий реестр. Однострочные конфликты решаются ребейзом
за секунды; правки по 20 строк в одном файле — нет.

---

## 6. Мины, на которых уже подорвались

- **Библиотека 1.5.0 уже содержит часть классов Destroy.** Перед портом «общего» класса из `core/`
  грепни джарник библиотеки. Точно не портировать:
  `ExtraInventorySizeChangePacket`, `RequestInventoryFullStatePacket`,
  `SetRedstoneProgramPacket`, `ChangeRedstoneProgrammerPowerPacket`,
  `RefreshRedstoneProgrammerScreenPacket`. Пакеты загрязнения уже есть в скелете.
- **Пакеты не переносятся отдельно от своих машин.** Проверено: все 9 сконвертированных пакетов
  не собрались, потому что тянут непортированных соседей. Пакет едет внутри кластера.
- **Массовый залив всего подряд не работает.** Проверено трижды: цикл «залить всё → выселить
  падающее» сходится к 375 файлам и не растёт. Единица переноса — кластер.
- **Ресурсы 1.21 переехали:** `loot_tables/` → `loot_table/`, `recipes/` → `recipe/`,
  в рецептах `result.item` → `result.id`. Молчаливая ошибка: рецепт просто не парсится,
  видно только в логе сервера.
- **Проверяй сервером, не только компилятором.** Так нашёлся непортированный `destroy:mesh`,
  из-за которого рецепт сита падал при парсинге.
- Пул-типы: `Pollution.PollutionType.SMOG` (enum) → `DestroyPollutionTypes.SMOG.get()` (реестр).
  `values()` и `.max` эквивалента не имеют — переписывать по месту.

---

## 7. Цикл работы одной сессии

```
1. Прочитать этот файл + свой раздел
2. Скопировать свой кластер целиком из port/wip в src/main/java
3. ./gradlew compileJava
4. Починить верхнюю ошибку (не обследовать — чинить)
5. → 3, пока не зелено
6. ./gradlew runServer, убедиться что грузится
7. git commit
8. Следующий кластер
```

Если файл упирается в чужой кластер — не тяни его к себе, оставь в `port/wip` и запиши в
`port/tools/blocked.md` строкой «файл — ждёт X».

## 8. Определение готовности

Мод считается портированным, когда: `port/wip` пуст (кроме `compat/`, `mixin/`, `test/`),
`./gradlew build` зелёный, сервер и клиент грузятся, `quarantined.tsv` пуст
(вся сериализация настоящая), и датаген прогнан один раз в конце (`./gradlew runData`).
