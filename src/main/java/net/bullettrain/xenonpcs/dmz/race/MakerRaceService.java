package net.bullettrain.xenonpcs.dmz.race;

import com.dragonminez.common.config.ConfigManager;
import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.SyncServerConfigS2C;
import com.google.gson.*;
import net.bullettrain.xenonpcs.XenoNpcsMod;
import net.bullettrain.xenonpcs.hair.HairMakerDocument;
import net.bullettrain.xenonpcs.network.maker.MakerNetwork;
import net.minecraft.server.level.ServerPlayer;
import java.nio.file.*;
import java.util.UUID;

/** Race writes run on the logical server. Custom-pack backups precede every update. */
public final class MakerRaceService {
    private static final Gson GSON = new Gson();
    private MakerRaceService() {}
    public static void execute(ServerPlayer player, MakerNetwork.Request request) {
        if (!player.hasPermissions(2)) { reply(player, request, false, "Operator level 2 is required.", new byte[0]); return; }
        var validation = RacePackService.validateRaceId(request.race());
        if (!validation.ok() || request.race().length() > 32) { reply(player, request, false, "Invalid race id.", new byte[0]); return; }
        String race = request.race().toLowerCase(java.util.Locale.ROOT);
        Path directory = RacePackService.dmzRoot().resolve("races").resolve(race);
        byte[] previous = null;
        Path staging = null;
        try {
            if (request.json().length() > MakerNetwork.MAX_JSON) throw new IllegalArgumentException("Maker request is too large");
            JsonObject json = JsonParser.parseString(request.json()).getAsJsonObject();
            if (request.operation() != MakerNetwork.Operation.CREATE) {
                if (!RacePackService.isCustomPack(race)) throw new IllegalArgumentException("Built-in race packs are read-only");
                previous = MakerRaceTransfer.snapshot(directory);
                String expected = json.has("expectedRevision") ? json.get("expectedRevision").getAsString() : "";
                if (!MakerRaceTransfer.revision(previous).equals(expected)) throw new IllegalArgumentException("Race changed on server. Reopen the maker before saving.");
                Path backup = RacePackService.dmzRoot().getParent().resolve("xenonpcs/race-backups").resolve(race).resolve(request.id() + ".zip");
                Files.createDirectories(backup.getParent()); Files.write(backup, previous, StandardOpenOption.CREATE_NEW);
            }
            if (request.operation() == MakerNetwork.Operation.CREATE && Files.exists(directory))
                throw new IllegalArgumentException("Race folder already exists; use a new id.");
            Path stagingRoot = RacePackService.dmzRoot().getParent().resolve("xenonpcs/maker-staging").toAbsolutePath().normalize();
            Files.createDirectories(stagingRoot);
            staging = Files.createTempDirectory(stagingRoot, "race-");
            if (previous != null) MakerRaceTransfer.install(staging.resolve("races").resolve(race), previous);
            String message;
            switch (request.operation()) {
                case CREATE, UPDATE -> {
                    RacePackTemplate template = GSON.fromJson(json.get("template"), RacePackTemplate.class);
                    var parts = GSON.fromJson(json.get("parts"), RacePackService.RacePartDefaults.class);
                    var labels = GSON.fromJson(json.get("labels"), RaceLabels.class);
                    var result = request.operation() == MakerNetwork.Operation.CREATE
                            ? RacePackService.createRacePack(staging, race, template, parts, labels)
                            : RacePackService.updateRacePack(staging, race, template, parts, labels);
                    if (!result.ok()) throw new IllegalArgumentException(result.message());
                    message = "Saved race " + race + ".";
                }
                case ADD_BODY -> {
                    var catalog = RaceAppearanceCatalog.load(staging, race);
                    var body = catalog.addGeneratedBody(staging, json.get("gender").getAsString(), json.get("color").getAsString());
                    catalog.save(staging); message = "Added " + body.label() + ".";
                }
                case SAVE_HAIR -> {
                    var catalog = RaceAppearanceCatalog.load(staging, race);
                    var document = HairMakerDocument.fromCatalogJson(json.getAsJsonObject("document"));
                    String style = json.has("style") ? json.get("style").getAsString() : "";
                    if (!style.isBlank() && catalog.hairById(style) != null) catalog.updateHairStyle(staging, style, document);
                    else catalog.addHairStyle(staging, document.name(), document);
                    catalog.save(staging); message = "Saved race hair.";
                }
                default -> throw new IllegalArgumentException("Unsupported maker operation");
            }
            byte[] snapshot = MakerRaceTransfer.snapshot(staging.resolve("races").resolve(race));
            MakerRaceTransfer.install(directory, snapshot);
            ConfigManager.reload(); RaceLabelRegistry.loadFromDisk();
            snapshot = MakerRaceTransfer.snapshot(directory); // Include DMZ-generated defaults in the revision sent to clients.
            var response = new MakerNetwork.Response(request.id(), true, race, message, snapshot);
            for (ServerPlayer viewer : player.getServer().getPlayerList().getPlayers()) {
                MakerNetwork.send(viewer, response);
                String key = "races/" + race + "/character";
                String config = ConfigManager.getSpecificConfigJson(key);
                if (config != null) NetworkHandler.sendToPlayer(new SyncServerConfigS2C(key, config, false), viewer);
            }
            net.bullettrain.xenonpcs.network.race.RaceLabelNetwork.broadcast(player);
        } catch (Exception failure) {
            XenoNpcsMod.LOGGER.warn("Maker race operation refused for {}", race, failure);
            reply(player, request, false, failure.getMessage() == null ? "Race save failed." : failure.getMessage(), new byte[0]);
        } finally {
            if (staging != null) {
                // This unique directory was created by this operation; never clean the live race directory.
                Path allowed = RacePackService.dmzRoot().getParent().resolve("xenonpcs/maker-staging").toAbsolutePath().normalize();
                if (staging.toAbsolutePath().normalize().getParent().equals(allowed)) {
                    try (var paths = Files.walk(staging)) {
                        for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
                    } catch (Exception cleanup) { XenoNpcsMod.LOGGER.warn("Could not remove maker staging directory {}", staging, cleanup); }
                }
            }
        }
    }
    public static void sendSnapshots(ServerPlayer player) {
        for (String race : RacePackService.knownRaceIds()) {
            if (!RacePackService.isCustomPack(race)) continue;
            try { MakerNetwork.send(player, new MakerNetwork.Response(new UUID(0, 0), true, race, "Race catalog synchronized.",
                    MakerRaceTransfer.snapshot(RacePackService.dmzRoot().resolve("races").resolve(race)))); }
            catch (Exception error) { XenoNpcsMod.LOGGER.warn("Could not synchronize maker race {}", race, error); }
        }
    }
    private static void reply(ServerPlayer player, MakerNetwork.Request request, boolean success, String message, byte[] data) {
        String bounded = message.length() > 1024 ? message.substring(0, 1024) : message;
        MakerNetwork.send(player, new MakerNetwork.Response(request.id(), success, request.race(), bounded, data));
    }
}
