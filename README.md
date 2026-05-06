# Minecraft Forge 1.20 - Série YouTube

Série de vídeos focada no desenvolvimento de mods para Minecraft usando Forge 1.20.1. Cada episódio implementa uma funcionalidade completa do mod, do início ao fim.

## Episódios

| EP | Funcionalidade | Vídeo |
|----|---------------|-------|
| 01 | Initial Setup and Mod Structure (Forge 1.20.1) | [▶ Assistir](https://youtu.be/qoeWvpQGW1Y) |
| 02 | Blocks, WorldGen and Project Evolution | [▶ Assistir](https://youtu.be/3rJfjRoLccE) |
| 03 | Custom Armor and Crafting System | [▶ Assistir](https://youtu.be/X-Yhg2fV6tc) |
| 04 | Cesium-137 Radiation System (Real Mechanics) | [▶ Assistir](https://youtu.be/hOsEEa-QX7E) |
| 05 | Refining Machine + Radioactive Grenade | [▶ Assistir](https://youtu.be/ni5vgd8rMlU) |
| 06 | Radioactive Mobs and Custom AI | [▶ Assistir](https://youtu.be/tRrHAhVPGME) |
| 07 | Multiblock Dimensional Portal + Knight Armor | [▶ Assistir](https://www.youtube.com/watch?v=bSVIM9iAY10) |

## EP07 — destaques

- **Portal Dimensional** como multiblock 3×3 (vertical), formado por Engineering Frames + portas de Energy/Item
- **Item `Shape`** pra exportar pattern de multiblock como JSON (marca 2 cantos no mundo, sneak+rclick gera schema)
- **Wrench** pra ações de assembly/disassembly do multiblock
- **Visual swap**: ao formar, controller troca por bloco visual grande (BlockBench-style); auxiliares viram filler invisível com colisão preservada
- **Capabilities delegadas**: cabos plugados nas portas (Energy/Item) falam direto com o controller via `MultiblockPortBlockEntity`
- **Skeleton em NBT**: blocos originais salvos no controller; ao quebrar qualquer aux, restaura o esqueleto inteiro
- **Knight Armor + Knight Sword** — set de cavaleiro com modelo customizado
- **Creative Energy Block** pra testes de FE em creative

Atualizações no Mutante:
- Modelo refeito com hierarquia simplificada (head4/boca/boca2 sob `headCenter`), textura 128×128
- Animações redesenhadas (`idle`, `walk`, `atackMelee`, `spin` com 7 voltas completas)

## Fluxo de Trabalho

1. Cada EP implementa uma feature completa e testável
2. Ao final do EP: commit no `master` + tag `EP{N}` (ex: `EP07`)
3. Histórico de commits reflete a progressão da série, e cada tag aponta pro estado do repo no fim daquele vídeo

## Tecnologias

- **Minecraft Forge** 1.20.1
- **Java** 17
- **Gradle**
- **BlockBench** pros modelos custom (entities + blocks)

---

# Minecraft Forge 1.20 - YouTube Series (English)

A YouTube series focused on developing Minecraft mods using Forge 1.20.1. Each episode implements one complete feature of the mod, end to end.

## Episodes

| EP | Feature | Video |
|----|---------|-------|
| 01 | Initial Setup and Mod Structure (Forge 1.20.1) | [▶ Watch](https://youtu.be/qoeWvpQGW1Y) |
| 02 | Blocks, WorldGen and Project Evolution | [▶ Watch](https://youtu.be/3rJfjRoLccE) |
| 03 | Custom Armor and Crafting System | [▶ Watch](https://youtu.be/X-Yhg2fV6tc) |
| 04 | Cesium-137 Radiation System (Real Mechanics) | [▶ Watch](https://youtu.be/hOsEEa-QX7E) |
| 05 | Refining Machine + Radioactive Grenade | [▶ Watch](https://youtu.be/ni5vgd8rMlU) |
| 06 | Radioactive Mobs and Custom AI | [▶ Watch](https://youtu.be/tRrHAhVPGME) |
| 07 | Multiblock Dimensional Portal + Knight Armor | [▶ Watch](https://www.youtube.com/watch?v=bSVIM9iAY10) |

## EP07 — highlights

- **Dimensional Portal** as a 3×3 vertical multiblock, formed by Engineering Frames + Energy/Item ports
- **`Shape` item** to export multiblock patterns as JSON (mark 2 corners in the world, sneak+rclick generates the schema)
- **Wrench** for multiblock assembly/disassembly actions
- **Visual swap**: once formed, the controller block is replaced by a large visual block (BlockBench-style); auxiliaries become invisible fillers with collision preserved
- **Delegated capabilities**: cables plugged into the ports (Energy/Item) talk directly to the controller via `MultiblockPortBlockEntity`
- **Skeleton in NBT**: original blocks are saved on the controller; breaking any aux restores the entire skeleton
- **Knight Armor + Knight Sword** — knight gear set with a custom model
- **Creative Energy Block** for FE testing in creative

Mutant updates:
- Model rebuilt with a simplified hierarchy (head4/boca/boca2 under `headCenter`), 128×128 texture
- Animations redesigned (`idle`, `walk`, `atackMelee`, `spin` with 7 full rotations)

## Workflow

1. Each EP implements one complete, testable feature
2. At the end of each EP: commit on `master` + tag `EP{N}` (e.g., `EP07`)
3. Commit history reflects the series progression, and each tag points to the state of the repo at the end of that video

## Technologies

- **Minecraft Forge** 1.20.1
- **Java** 17
- **Gradle**
- **BlockBench** for custom models (entities + blocks)
