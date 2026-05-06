package com.navaronee.meuprimeiromod.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Shape item — ferramenta de design de multiblock.
 *
 * Workflow:
 *  1. Right-click bloco → seta corner1
 *  2. Right-click outro bloco → seta corner2
 *  3. Sneak+right-click qualquer bloco → exporta JSON com todos os blocos no
 *     volume entre corner1 e corner2 pra run/exports/shape_<n>.json. Limpa cantos.
 *
 * Tooltip mostra cantos atuais.
 */
public class ShapeItem extends Item {

    private static final String TAG_C1 = "Corner1";
    private static final String TAG_C2 = "Corner2";

    public ShapeItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        ItemStack stack = ctx.getItemInHand();
        CompoundTag tag = stack.getOrCreateTag();

        if (player.isShiftKeyDown()) {
            // Export
            if (!tag.contains(TAG_C1) || !tag.contains(TAG_C2)) {
                if (!level.isClientSide()) {
                    player.displayClientMessage(Component.literal("§cMarque os 2 cantos primeiro (right-click)."), false);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            if (!level.isClientSide()) {
                BlockPos c1 = NbtUtils.readBlockPos(tag.getCompound(TAG_C1));
                BlockPos c2 = NbtUtils.readBlockPos(tag.getCompound(TAG_C2));
                String filename = exportSchema(level, c1, c2);
                player.displayClientMessage(Component.literal("§aShape exportado: §e" + filename), false);
                tag.remove(TAG_C1);
                tag.remove(TAG_C2);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // Marca corner
        if (!level.isClientSide()) {
            if (!tag.contains(TAG_C1)) {
                tag.put(TAG_C1, NbtUtils.writeBlockPos(pos));
                player.displayClientMessage(Component.literal("§eCorner 1 setado em " + posStr(pos)), true);
            } else if (!tag.contains(TAG_C2)) {
                tag.put(TAG_C2, NbtUtils.writeBlockPos(pos));
                player.displayClientMessage(Component.literal("§eCorner 2 setado em " + posStr(pos) + " §7(sneak+rclick pra exportar)"), true);
            } else {
                // Re-marca corner1, limpa corner2
                tag.put(TAG_C1, NbtUtils.writeBlockPos(pos));
                tag.remove(TAG_C2);
                player.displayClientMessage(Component.literal("§eRecome\u00e7ando: corner 1 setado em " + posStr(pos)), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    /**
     * Exporta o volume entre c1 e c2 como JSON pra run/exports/shape_<n>.json.
     * Coords no JSON s\u00e3o relativas ao corner inferior do volume (origem 0,0,0).
     */
    private String exportSchema(Level level, BlockPos c1, BlockPos c2) {
        int minX = Math.min(c1.getX(), c2.getX());
        int minY = Math.min(c1.getY(), c2.getY());
        int minZ = Math.min(c1.getZ(), c2.getZ());
        int maxX = Math.max(c1.getX(), c2.getX());
        int maxY = Math.max(c1.getY(), c2.getY());
        int maxZ = Math.max(c1.getZ(), c2.getZ());

        JsonObject root = new JsonObject();
        JsonArray sizeArr = new JsonArray();
        sizeArr.add(maxX - minX + 1);
        sizeArr.add(maxY - minY + 1);
        sizeArr.add(maxZ - minZ + 1);
        root.add("size", sizeArr);

        JsonArray blocksArr = new JsonArray();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockState st = level.getBlockState(new BlockPos(x, y, z));
                    if (st.isAir()) continue;
                    JsonObject entry = new JsonObject();
                    JsonArray relPos = new JsonArray();
                    relPos.add(x - minX);
                    relPos.add(y - minY);
                    relPos.add(z - minZ);
                    entry.add("pos", relPos);
                    ResourceLocation id = BuiltInRegistries.BLOCK.getKey(st.getBlock());
                    entry.addProperty("block", id.toString());
                    blocksArr.add(entry);
                }
            }
        }
        root.add("blocks", blocksArr);

        // Escreve em run/exports/shape_<n>.json
        Path exportsDir = Paths.get("exports");
        try {
            Files.createDirectories(exportsDir);
        } catch (IOException ignored) {}
        int n = 1;
        Path target;
        do {
            target = exportsDir.resolve("shape_" + n + ".json");
            n++;
        } while (Files.exists(target) && n < 9999);

        try (FileWriter w = new FileWriter(target.toFile())) {
            w.write(new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(root));
        } catch (IOException e) {
            return "ERRO: " + e.getMessage();
        }
        return target.getFileName().toString();
    }

    private static String posStr(BlockPos p) {
        return "(" + p.getX() + "," + p.getY() + "," + p.getZ() + ")";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                 List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tooltip.add(Component.literal("Right-click 2 cantos, sneak+rclick pra exportar.")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        if (tag.contains(TAG_C1)) {
            BlockPos c1 = NbtUtils.readBlockPos(tag.getCompound(TAG_C1));
            tooltip.add(Component.literal("Corner 1: " + posStr(c1)).withStyle(ChatFormatting.GREEN));
        }
        if (tag.contains(TAG_C2)) {
            BlockPos c2 = NbtUtils.readBlockPos(tag.getCompound(TAG_C2));
            tooltip.add(Component.literal("Corner 2: " + posStr(c2)).withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.literal("Sneak+rclick pra exportar").withStyle(ChatFormatting.YELLOW));
        }
    }
}
