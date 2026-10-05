package net.bullettrain.xenonpcs.client.maker;

import com.google.gson.*;
import net.bullettrain.xenonpcs.dmz.race.*;
import net.bullettrain.xenonpcs.network.maker.MakerNetwork;
import java.util.*;
import java.util.function.Consumer;

/** Requests server race edits; the server snapshot refreshes the local preview catalog. */
public final class MakerRaceClient {
    public static final Gson GSON = new Gson();
    private static final Map<UUID, Consumer<MakerNetwork.Response>> PENDING = new HashMap<>();
    private static final Map<String, String> REVISIONS = new HashMap<>();
    private static final Set<String> BACKED_UP = new HashSet<>();
    private MakerRaceClient() {}
    public static void request(MakerNetwork.Operation operation, String race, JsonObject data,
                               Consumer<MakerNetwork.Response> callback) {
        UUID id = UUID.randomUUID();
        if (operation != MakerNetwork.Operation.CREATE) {
            String revision = REVISIONS.get(race);
            if (revision == null && net.minecraft.client.Minecraft.getInstance().hasSingleplayerServer()) {
                try { revision = MakerRaceTransfer.revision(MakerRaceTransfer.snapshot(RacePackService.dmzRoot().resolve("races").resolve(race))); }
                catch (Exception ignored) { }
            }
            data.addProperty("expectedRevision", revision == null ? "" : revision);
        }
        if (PENDING.size() >= 32) { callback.accept(new MakerNetwork.Response(id, false, race, "Too many pending maker requests. Reconnect if the server is unresponsive.", new byte[0])); return; }
        PENDING.put(id, callback);
        MakerNetwork.request(id, operation, race, GSON.toJson(data));
    }
    public static void accept(MakerNetwork.Response response) {
        var applied = response;
        try {
            if (response.success() && response.archive().length > 0) {
                if (!RacePackService.validateRaceId(response.race()).ok()) throw new IllegalArgumentException("Invalid race snapshot id");
                // Integrated games already share the authoritative server files.
                if (!net.minecraft.client.Minecraft.getInstance().hasSingleplayerServer()) {
                    var directory = RacePackService.dmzRoot().resolve("races").resolve(response.race());
                    if (java.nio.file.Files.isDirectory(directory) && BACKED_UP.add(response.race())) {
                        var backup = RacePackService.dmzRoot().getParent().resolve("xenonpcs/client-race-backups")
                                .resolve(response.race() + "-" + UUID.randomUUID() + ".zip");
                        java.nio.file.Files.createDirectories(backup.getParent());
                        java.nio.file.Files.write(backup, MakerRaceTransfer.snapshot(directory), java.nio.file.StandardOpenOption.CREATE_NEW);
                    }
                    MakerRaceTransfer.install(directory, response.archive());
                }
                REVISIONS.put(response.race(), MakerRaceTransfer.revision(response.archive()));
                // Apply only this server race; a global reload would discard unrelated synced server configs.
                for (var entry : MakerRaceTransfer.decode(response.archive()).entrySet()) {
                    String file = entry.getKey();
                    if (file.equals("character.json") || file.equals("stats.json") || file.startsWith("forms/") && file.endsWith(".json")) {
                        com.dragonminez.common.config.ConfigManager.applySpecificSyncedConfig("races/" + response.race() + "/" + file.substring(0, file.length() - 5),
                                new String(entry.getValue(), java.nio.charset.StandardCharsets.UTF_8));
                    }
                }
                var catalog = RaceAppearanceCatalog.loadLive(response.race());
                RaceAssetPack.sync(RacePackService.dmzRoot(), catalog);
                RaceAssetPackClient.reload(); RaceLabelRegistry.loadFromDisk();
            }
        } catch (Exception failure) {
            applied = new MakerNetwork.Response(response.id(), false, response.race(), "Saved on server; client catalog reload failed: " + failure.getMessage(), new byte[0]);
        }
        var callback = PENDING.remove(response.id());
        if (callback != null) callback.accept(applied);
    }
    public static void clear() { PENDING.clear(); REVISIONS.clear(); BACKED_UP.clear(); }
}
