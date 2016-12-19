package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import org.bukkit.block.Block;

@RequiredArgsConstructor
public class BlockRegion {
    final static String FOLDER = "Winthier.Custom";
    final static Comparator<BlockRegion> LAST_SAVE_COMPARATOR = new Comparator<BlockRegion>() {
        @Override
        public int compare(BlockRegion a, BlockRegion b) {
            return Long.compare(a.lastSave, b.lastSave);
        }
    };

    @Value
    static class Vector {
        int x, z;

        final static int ofBlock(int v) {
            if (v < 0) {
                return (v + 1) / 512 - 1;
            } else {
                return v / 512;
            }
        }

        static Vector of(Block block) {
            return new Vector(ofBlock(block.getX()), ofBlock(block.getZ()));
        }
    }

    final BlockWorld blockWorld;
    final Vector position;
    final Map<BlockChunk.Vector, BlockChunk> chunks = new HashMap<>();

    @Getter @Setter transient long lastSave = System.currentTimeMillis();

    BlockChunk getBlockChunk(BlockChunk.Vector position) {
        BlockChunk result = chunks.get(position);
        if (result == null) {
            result = new BlockChunk(blockWorld, this, position);
            chunks.put(position, result);
        }
        return result;
    }

    final String getFileName() {
        return String.format("Region.%d.%d.Custom", position.x, position.z);
    }

    void load() {
        File dir = new File(blockWorld.world.getWorldFolder(), FOLDER);
        if (!dir.isDirectory()) return;
        File file = new File(dir, getFileName());
        if (!file.isFile()) return;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            BlockChunk chunk = null;
            while (null != (line = in.readLine())) {
                if (line.isEmpty()) {
                } else if (line.startsWith("Chunk;")) {
                    String[] tokens = line.split(";", 3);
                    int x = Integer.parseInt(tokens[1]);
                    int z = Integer.parseInt(tokens[2]);
                    BlockChunk.Vector chunkPosition = new BlockChunk.Vector(x, z);
                    chunk = new BlockChunk(blockWorld, this, chunkPosition);
                } else if (line.startsWith("Block;")) {
                    String[] tokens = line.split(";", 6);
                    int x = Integer.parseInt(tokens[1]);
                    int y = Integer.parseInt(tokens[2]);
                    int z = Integer.parseInt(tokens[3]);
                    BlockVector position = BlockVector.of(x, y, z);
                    String customId = tokens[4];
                    String json = tokens[5];
                    CustomConfig config = new CustomConfig(customId, json);
                    chunk.configs.put(position, config);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    void save() {
        File dir = new File(blockWorld.world.getWorldFolder(), FOLDER);
        dir.mkdirs();
        File file = new File(dir, getFileName());
        try {
            PrintStream out = new PrintStream(file);
            for (BlockChunk chunk: chunks.values()) {
                out.format("Chunk;%d;%d\n", chunk.position.getX(), chunk.position.getZ());
                for (Map.Entry<BlockVector, CustomConfig> entry: chunk.configs.entrySet()) {
                    BlockVector vector = entry.getKey();
                    CustomConfig config = entry.getValue();
                    out.format("Block;%d;%d;%d;%s;%s\n", vector.getX(), vector.getY(), vector.getZ(), config.getCustomId(), config.getJsonString());
                }
            }
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    void setDirty() {
        blockWorld.blockManager.regionSaveList.add(this);
    }
}
