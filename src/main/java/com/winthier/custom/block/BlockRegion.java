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
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
final class BlockRegion {
    @Value
    static final class Vector {
        private final int x, z;

        static int ofChunk(int v) {
            if (v < 0) {
                return (v + 1) / 32 - 1;
            } else {
                return v / 32;
            }
        }

        static Vector of(BlockChunk.Vector chunk) {
            return new Vector(ofChunk(chunk.getX()), ofChunk(chunk.getZ()));
        }
    }

    static final String FOLDER = "Winthier.Custom";
    static final Comparator<BlockRegion> LAST_SAVE_COMPARATOR = new Comparator<BlockRegion>() {
        @Override
        public int compare(BlockRegion a, BlockRegion b) {
            return Long.compare(a.lastSave, b.lastSave);
        }
    };
    private final BlockWorld blockWorld;
    private final Vector position;
    private final Map<BlockChunk.Vector, BlockChunk> chunks = new HashMap<>();
    long lastSave = 0;

    BlockChunk getBlockChunk(BlockChunk.Vector chunkPosition) {
        BlockChunk result = chunks.get(chunkPosition);
        if (result == null) {
            result = new BlockChunk(blockWorld, this, chunkPosition);
            chunks.put(chunkPosition, result);
        }
        return result;
    }

    String getFileName() {
        return String.format("Region.%d.%d.Custom", position.x, position.z);
    }

    void load() {
        File dir = new File(blockWorld.world.getWorldFolder(), FOLDER);
        File file = new File(dir, getFileName());
        if (!file.isFile()) return;
        if (!dir.isDirectory()) return;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            BlockChunk chunk = null;
            while (null != (line = in.readLine())) {
                if (line.isEmpty()) {
                    continue;
                } else if (line.startsWith("Chunk;")) {
                    String[] tokens = line.split(";", 3);
                    int x = Integer.parseInt(tokens[1]);
                    int z = Integer.parseInt(tokens[2]);
                    BlockChunk.Vector chunkPosition = new BlockChunk.Vector(x, z);
                    chunk = new BlockChunk(blockWorld, this, chunkPosition);
                    chunks.put(chunkPosition, chunk);
                } else if (line.startsWith("Block;")) {
                    String[] tokens = line.split(";", 6);
                    int x = Integer.parseInt(tokens[1]);
                    int y = Integer.parseInt(tokens[2]);
                    int z = Integer.parseInt(tokens[3]);
                    BlockVector blockPosition = BlockVector.of(x, y, z);
                    String customId = tokens[4];
                    String json = tokens[5];
                    CustomConfig config = new CustomConfig(customId, json);
                    chunk.configs.put(blockPosition, config);
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
                if (chunk.configs.isEmpty()) continue;
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
}
