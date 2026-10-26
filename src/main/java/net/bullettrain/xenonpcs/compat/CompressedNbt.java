package net.bullettrain.xenonpcs.compat;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

/** Bounded compressed NBT reads on Forge, whose compressed helper has no quota parameter. */
public final class CompressedNbt {
    private CompressedNbt() {}
    public static CompoundTag read(Path path, NbtAccounter quota) throws IOException {
        try (var input = new DataInputStream(new GZIPInputStream(Files.newInputStream(path)))) {
            return NbtIo.read(input, quota);
        }
    }
}
