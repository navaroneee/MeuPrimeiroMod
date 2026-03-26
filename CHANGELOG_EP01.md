# EP01 - Setup do Ambiente + Primeiros Blocos

## Resumo
Primeiro episódio da série "Criando um Mod de Minecraft do Zero". Setup completo do ambiente de desenvolvimento Forge 1.20.1 com dois blocos funcionais.

## Ambiente Configurado

| Item | Versão/Detalhes |
|------|-----------------|
| Minecraft | 1.20.1 |
| Forge | 47.2.0 |
| JDK | 17.0.18 (Temurin/Adoptium) - instalado localmente em `jdk17/` |
| IDE | IntelliJ IDEA (configurado com run configs) |
| Mod ID | `meuprimeiromod` |
| Package | `com.navaronee.meuprimeiromod` |

## Estrutura de Arquivos Criada

```
src/main/
├── java/com/navaronee/meuprimeiromod/
│   ├── MeuPrimeiroMod.java      # Classe principal @Mod
│   ├── ModCreativeTabs.java     # Aba criativa exclusiva
│   ├── block/
│   │   └── ModBlocks.java       # Registro de blocos (com helper reutilizável)
│   └── item/
│       └── ModItems.java        # Registro de itens
│
└── resources/
    ├── assets/meuprimeiromod/
    │   ├── blockstates/
    │   │   ├── wood_chair.json
    │   │   └── lead_ore.json
    │   ├── models/
    │   │   ├── block/
    │   │   │   ├── wood_chair.json  # Modelo custom (Blockbench)
    │   │   │   └── lead_ore.json    # Modelo simples (cube_all)
    │   │   └── item/
    │   │       ├── wood_chair.json
    │   │       └── lead_ore.json
    │   ├── textures/block/
    │   │   └── lead_ore.png         # Textura 16x16 custom
    │   └── lang/
    │       ├── en_us.json
    │       └── pt_br.json
    │
    ├── data/meuprimeiromod/
    │   ├── loot_tables/blocks/
    │   │   ├── wood_chair.json
    │   │   └── lead_ore.json
    │   └── recipes/
    │       └── wood_chair.json      # 4 jungle_planks + 2 sticks
    │
    └── META-INF/mods.toml
```

## Blocos Adicionados

### 1. Wood Chair (Cadeira de Madeira)
- **Tipo**: Bloco custom com modelo Blockbench
- **Propriedades**: `strength(2f)`, `SoundType.WOOD`, `.noOcclusion()`
- **Textura**: Usa texturas vanilla (jungle_log, jungle_planks)
- **Modelo**: 7 cubos (4 pernas, 1 assento, 1 encosto, 1 perna alta)
- **Receita**: Shaped crafting (jungle_planks + sticks)

### 2. Lead Ore (Minério de Chumbo)
- **Tipo**: Bloco simples com `cube_all`
- **Propriedades**: `strength(3f)`, `SoundType.STONE`
- **Textura**: Custom 16x16 (`lead_ore.png`)
- **Modelo**: Parent `minecraft:block/cube_all`
- **Receita**: Nenhuma (será gerado no mundo em EP futuro)

## Sistemas Implementados

### DeferredRegister Pattern
```java
// Helper que registra bloco + item automaticamente
private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
    RegistryObject<T> toReturn = BLOCKS.register(name, block);
    registerBlockItem(name, toReturn);
    return toReturn;
}
```

### Creative Tab (1.20 style)
```java
CreativeModeTab.builder()
    .icon(() -> new ItemStack(ModBlocks.WOOD_CHAIR.get()))
    .title(Component.translatable("itemGroup.meuprimeiromod"))
    .displayItems((parameters, output) -> {
        output.accept(ModBlocks.WOOD_CHAIR.get());
        output.accept(ModBlocks.LEAD_ORE.get());
    })
    .build()
```

## Repositórios GitHub

| Repo | URL | Descrição |
|------|-----|-----------|
| MeuPrimeiroMod | github.com/navaroneee/MeuPrimeiroMod | Código completo da série |
| forge-1.20.1-base | github.com/navaroneee/forge-1.20.1-base | Template limpo para novos mods |

## Comandos Úteis

```bash
./gradlew build              # Compila o mod
./gradlew runClient          # Abre Minecraft com o mod
./gradlew genIntellijRuns    # Gera run configs para IntelliJ
```

## Problemas Resolvidos

1. **Java 8 vs Java 17**: Configurado `org.gradle.java.home` com caminho absoluto para JDK 17 local, sem interferir no Java 8 do sistema
2. **IntelliJ sem autocomplete**: Configurado SDK do projeto e Gradle JVM para JDK 17 no `.idea/gradle.xml` e `.idea/misc.xml`
3. **Linter removendo chaves**: Arquivos Java tinham `}` final removido pelo linter - corrigido manualmente

## Arquivos de Produção

- `ROTEIRO_EP01.txt` - Roteiro completo com 15 cenas para gravação do vídeo

---

**Próximo EP**: EP02 - [A definir]
