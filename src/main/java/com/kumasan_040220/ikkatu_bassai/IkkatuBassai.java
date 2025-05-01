package com.kumasan_040220.ikkatu_bassai;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

@Mod(IkkatuBassai.MODID)
public class IkkatuBassai {
    public static final String MODID = "ikkatu_bassai";
    private static final Logger LOGGER = LogUtils.getLogger();

    public IkkatuBassai() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Item heldItem = event.getPlayer().getMainHandItem().getItem();
        if (!isAxe(heldItem)) {
            return;
        }

        Block block = event.getState().getBlock();
        if (isLogBlock(block)) {
            Set<BlockPos> connectedLogs = findConnectedLogs(event.getLevel(), event.getPos());
            for (BlockPos pos : connectedLogs) {
                if (!pos.equals(event.getPos())) {
                    event.getLevel().destroyBlock(pos, true);
                }
            }
        }
    }

    private static boolean isAxe(Item item) {
        return item == Items.WOODEN_AXE ||
               item == Items.STONE_AXE ||
               item == Items.IRON_AXE ||
               item == Items.GOLDEN_AXE ||
               item == Items.DIAMOND_AXE ||
               item == Items.NETHERITE_AXE;
    }

    private static boolean isLogBlock(Block block) {
        return block == Blocks.OAK_LOG ||
               block == Blocks.SPRUCE_LOG ||
               block == Blocks.BIRCH_LOG ||
               block == Blocks.JUNGLE_LOG ||
               block == Blocks.ACACIA_LOG ||
               block == Blocks.DARK_OAK_LOG ||
               block == Blocks.MANGROVE_LOG ||
               block == Blocks.CHERRY_LOG ||
               block == Blocks.PALE_OAK_LOG;
    }

    private static boolean isLeafBlock(Block block) {
        return block == Blocks.OAK_LEAVES ||
               block == Blocks.SPRUCE_LEAVES ||
               block == Blocks.BIRCH_LEAVES ||
               block == Blocks.JUNGLE_LEAVES ||
               block == Blocks.ACACIA_LEAVES ||
               block == Blocks.DARK_OAK_LEAVES ||
               block == Blocks.MANGROVE_LEAVES ||
               block == Blocks.CHERRY_LEAVES ||
               block == Blocks.AZALEA_LEAVES ||
               block == Blocks.FLOWERING_AZALEA_LEAVES;
    }

    private Set<BlockPos> findConnectedLogs(LevelAccessor level, BlockPos startPos) {
        Set<BlockPos> connectedBlocks = new HashSet<>();
        Stack<BlockPos> toCheck = new Stack<>();
        toCheck.push(startPos);

        while (!toCheck.isEmpty()) {
            BlockPos current = toCheck.pop();
            if (connectedBlocks.contains(current)) continue;

            Block block = level.getBlockState(current).getBlock();
            if (isLogBlock(block) || isLeafBlock(block)) {
                connectedBlocks.add(current);
                // 3x3x3の立方体の中心を除く26方向をチェック
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            // 中心（0,0,0）はスキップ
                            if (x == 0 && y == 0 && z == 0) continue;
                            
                            BlockPos nextPos = current.offset(x, y, z);
                            if (!connectedBlocks.contains(nextPos)) {
                                toCheck.push(nextPos);
                            }
                        }
                    }
                }
            }
        }

        return connectedBlocks;
    }
} 