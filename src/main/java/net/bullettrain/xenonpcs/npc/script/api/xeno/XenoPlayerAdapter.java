package net.bullettrain.xenonpcs.npc.script.api.xeno;

import net.bullettrain.xenonpcs.npc.script.api.ScriptEntity;
import net.bullettrain.xenonpcs.npc.script.api.ScriptPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import xenoapi.npcs.api.IContainer;
import xenoapi.npcs.api.ITimers;
import xenoapi.npcs.api.block.IBlock;
import xenoapi.npcs.api.entity.IPlayer;
import xenoapi.npcs.api.entity.data.IData;
import xenoapi.npcs.api.entity.data.IPlayerMail;
import xenoapi.npcs.api.gui.ICustomGui;
import xenoapi.npcs.api.handler.data.IQuest;
import xenoapi.npcs.api.item.IItemStack;

import java.util.function.Predicate;

/**
 * A server player as XenoAPI's {@link IPlayer}. Quests, messages and script data go through the
 * native {@link ScriptPlayer}; inventory, experience, game mode and spawn use vanilla calls.
 * Quest ids are the native quest slots the {@code player} binding already uses.
 */
public final class XenoPlayerAdapter extends XenoLivingAdapter<ServerPlayer> implements IPlayer<ServerPlayer> {
    static final int MAX_ITEM_AMOUNT = 64 * 36;

    public XenoPlayerAdapter(ServerPlayer entity) {
        super(entity);
    }

    private ScriptPlayer scriptPlayer() {
        return (ScriptPlayer) ScriptEntity.of(entity);
    }

    // ------------------------------------------------------------------ messaging / identity

    @Override public String getDisplayName() { return entity.getDisplayName().getString(); }

    @Override
    public void message(String message) {
        if (net.bullettrain.xenonpcs.compat.npc.NpcScriptSay.sanitize(message) == null) {
            throw new IllegalArgumentException("IPlayer.message: message must be non-empty and within the chat limit");
        }
        serverThread();
        scriptPlayer().message(message);
    }

    @Override
    public boolean isOp() {
        var server = entity.getServer();
        return server != null && server.getPlayerList().isOp(entity.getGameProfile());
    }

    @Override
    public void kick(String message) {
        String reason = XenoApiAdapters.boundedText("IPlayer.kick", message, 256);
        serverThread();
        entity.connection.disconnect(Component.literal(reason.isEmpty() ? "Kicked" : reason));
    }

    @Override public void setName(String name) { throw XenoApiAdapters.unsupported("IPlayer.setName"); }

    // ------------------------------------------------------------------ quests (native slots)

    @Override public boolean hasFinishedQuest(int id) { return scriptPlayer().hasFinishedQuest(id); }
    @Override public boolean hasActiveQuest(int id) { return scriptPlayer().hasActiveQuest(id); }

    @Override
    public void startQuest(int id) {
        serverThread();
        scriptPlayer().startQuest(id);
    }

    @Override
    public void finishQuest(int id) {
        serverThread();
        scriptPlayer().finishQuest(id);
    }

    @Override
    public void stopQuest(int id) {
        serverThread();
        scriptPlayer().stopQuest(id);
    }

    // ------------------------------------------------------------------ script data

    @Override public IData getTempdata() { return XenoDataAdapter.ofPlayer(() -> scriptPlayer().getTempdata()); }
    @Override public IData getStoreddata() { return XenoDataAdapter.ofPlayer(() -> scriptPlayer().getStoreddata()); }

    // ------------------------------------------------------------------ game mode / xp / food

    @Override public int getGamemode() { return entity.gameMode.getGameModeForPlayer().getId(); }

    @Override
    public void setGamemode(int mode) {
        if (mode < 0 || mode > 3) throw new IllegalArgumentException("IPlayer.setGamemode: mode must be 0-3");
        serverThread();
        entity.setGameMode(GameType.byId(mode));
    }

    @Override public int getExpLevel() { return entity.experienceLevel; }

    @Override
    public void setExpLevel(int level) {
        if (level < 0 || level > 1_000_000) throw new IllegalArgumentException("IPlayer.setExpLevel: level must be 0-1000000");
        serverThread();
        entity.setExperienceLevels(level);
    }

    @Override public int getHunger() { return entity.getFoodData().getFoodLevel(); }

    @Override
    public void setHunger(int level) {
        serverThread();
        entity.getFoodData().setFoodLevel(Math.max(0, Math.min(20, level)));
    }

    @Override
    public boolean hasAdvancement(String achievement) {
        ResourceLocation id = achievement == null ? null : ResourceLocation.tryParse(achievement);
        var server = entity.getServer();
        if (id == null || server == null) return false;
        var holder = server.getAdvancements().get(id);
        return holder != null && entity.getAdvancements().getOrStartProgress(holder).isDone();
    }

    // ------------------------------------------------------------------ spawn point

    @Override
    public void setSpawnpoint(int x, int y, int z) {
        serverThread();
        entity.setRespawnPosition(entity.level().dimension(), new BlockPos(x, y, z), 0.0f, true, false);
    }

    @Override
    public void resetSpawnpoint() {
        serverThread();
        entity.setRespawnPosition(Level.OVERWORLD, null, 0.0f, false, false);
    }

    // ------------------------------------------------------------------ inventory

    private static Predicate<ItemStack> matches(IItemStack item) {
        ItemStack wanted = XenoApiAdapters.unwrap(item);
        if (wanted.isEmpty()) throw new IllegalArgumentException("Item cannot be empty");
        return stack -> ItemStack.isSameItemSameComponents(stack, wanted);
    }

    private static Predicate<ItemStack> matches(String id) {
        var item = XenoApiAdapters.knownItem(id);
        return item == null ? stack -> false : stack -> stack.is(item);
    }

    private int count(Predicate<ItemStack> filter) {
        int total = 0;
        var inventory = entity.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty() && filter.test(stack)) total += stack.getCount();
        }
        return total;
    }

    /** Removes exactly {@code amount} matching items, or nothing when there are not enough. */
    private boolean remove(Predicate<ItemStack> filter, int amount) {
        if (amount < 1 || amount > MAX_ITEM_AMOUNT) throw new IllegalArgumentException("Amount must be 1-" + MAX_ITEM_AMOUNT);
        serverThread();
        if (count(filter) < amount) return false;
        var inventory = entity.getInventory();
        int left = amount;
        for (int slot = 0; slot < inventory.getContainerSize() && left > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty() || !filter.test(stack)) continue;
            int taken = Math.min(left, stack.getCount());
            stack.shrink(taken);
            left -= taken;
        }
        inventory.setChanged();
        return true;
    }

    @Override public int inventoryItemCount(IItemStack item) { return count(matches(item)); }
    @Override public int inventoryItemCount(String id) { return count(matches(id)); }
    @Override public boolean removeItem(IItemStack item, int amount) { return remove(matches(item), amount); }
    /** False for an unknown item id, as the reference documents. */
    @Override
    public boolean removeItem(String id, int amount) {
        if (XenoApiAdapters.knownItem(id) == null) return false;
        return remove(matches(id), amount);
    }

    @Override
    public void removeAllItems(IItemStack item) {
        Predicate<ItemStack> filter = matches(item);
        serverThread();
        entity.getInventory().clearOrCountMatchingItems(filter, -1, entity.inventoryMenu.getCraftSlots());
    }

    @Override public IItemStack getInventoryHeldItem() { return XenoApiAdapters.wrap(entity.getMainHandItem()); }

    @Override
    public boolean giveItem(IItemStack item) {
        ItemStack stack = XenoApiAdapters.unwrap(item).copy();
        if (stack.isEmpty()) return false;
        serverThread();
        return entity.getInventory().add(stack);
    }

    @Override
    public boolean giveItem(String id, int amount) {
        var item = XenoApiAdapters.item(id);
        if (amount < 1 || amount > item.getDefaultMaxStackSize()) {
            throw new IllegalArgumentException("IPlayer.giveItem: amount must be 1-" + item.getDefaultMaxStackSize());
        }
        serverThread();
        return entity.getInventory().add(new ItemStack(item, amount));
    }

    @Override
    public void giveOrDropItems(IItemStack[] items) {
        if (items == null) return;
        ItemStack[] stacks = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) stacks[i] = XenoApiAdapters.unwrap(items[i]).copy();
        serverThread();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty() && !entity.getInventory().add(stack)) entity.drop(stack, false);
        }
    }

    @Override
    public void updatePlayerInventory() {
        serverThread();
        entity.inventoryMenu.broadcastChanges();
    }

    @Override
    public void closeGui() {
        serverThread();
        entity.closeContainer();
    }

    // ------------------------------------------------------------------ sound

    @Override
    public void playSound(String sound, float volume, float pitch) {
        SoundEvent event = XenoApiAdapters.sound(sound);
        XenoApiAdapters.requireFinite("IPlayer.playSound", volume, pitch);
        serverThread();
        entity.playNotifySound(event, SoundSource.MASTER, Math.max(0.0f, Math.min(4.0f, volume)),
                Math.max(0.5f, Math.min(2.0f, pitch)));
    }

    // ------------------------------------------------------------------ unsupported

    @Override public int factionStatus(int factionId) { throw XenoApiAdapters.unsupported("IPlayer.factionStatus"); }
    @Override public void removeQuest(int id) { throw XenoApiAdapters.unsupported("IPlayer.removeQuest"); }
    @Override public boolean hasReadDialog(int id) { throw XenoApiAdapters.unsupported("IPlayer.hasReadDialog"); }
    @Override public void showDialog(int id, String name) { throw XenoApiAdapters.unsupported("IPlayer.showDialog"); }
    @Override public void removeDialog(int id) { throw XenoApiAdapters.unsupported("IPlayer.removeDialog"); }
    @Override public void addDialog(int id) { throw XenoApiAdapters.unsupported("IPlayer.addDialog"); }
    @Override public void addFactionPoints(int faction, int points) { throw XenoApiAdapters.unsupported("IPlayer.addFactionPoints"); }
    @Override public int getFactionPoints(int faction) { throw XenoApiAdapters.unsupported("IPlayer.getFactionPoints"); }
    @Override public IContainer getInventory() { return XenoContainerAdapter.of(entity.getInventory()); }
    @Override public boolean hasPermission(String permission) { throw XenoApiAdapters.unsupported("IPlayer.hasPermission"); }
    @Override public Object getPixelmonData() { throw XenoApiAdapters.unsupported("IPlayer.getPixelmonData"); }
    @Override
    public ITimers getTimers() {
        serverThread();
        return XenoTimersAdapter.forTimers(net.bullettrain.xenonpcs.npc.script.PlayerScriptTimers.of(entity),
                () -> entity.level().getGameTime());
    }
    @Override public IBlock getSpawnPoint() { throw XenoApiAdapters.unsupported("IPlayer.getSpawnPoint"); }
    @Override public void setSpawnPoint(IBlock block) { throw XenoApiAdapters.unsupported("IPlayer.setSpawnPoint (use setSpawnpoint(x, y, z))"); }
    @Override public void sendNotification(String title, String msg, int type) { throw XenoApiAdapters.unsupported("IPlayer.sendNotification"); }
    @Override public void sendMail(IPlayerMail mail) { throw XenoApiAdapters.unsupported("IPlayer.sendMail"); }
    @Override public void clearData() { throw XenoApiAdapters.unsupported("IPlayer.clearData"); }
    @Override public IQuest[] getActiveQuests() { throw XenoApiAdapters.unsupported("IPlayer.getActiveQuests (use hasActiveQuest(slot))"); }
    @Override public IQuest[] getFinishedQuests() { throw XenoApiAdapters.unsupported("IPlayer.getFinishedQuests (use hasFinishedQuest(slot))"); }
    @Override public void playMusic(String sound, boolean background, boolean loops) { throw XenoApiAdapters.unsupported("IPlayer.playMusic"); }
    @Override public void stopMusic() { throw XenoApiAdapters.unsupported("IPlayer.stopMusic"); }
    @Override public IContainer getOpenContainer() { return XenoContainerAdapter.of(entity.containerMenu); }
    @Override public boolean canQuestBeAccepted(int id) { throw XenoApiAdapters.unsupported("IPlayer.canQuestBeAccepted"); }
    @Override public void showCustomGui(ICustomGui gui) { throw XenoApiAdapters.unsupported("IPlayer.showCustomGui"); }
    @Override public ICustomGui getCustomGui() { throw XenoApiAdapters.unsupported("IPlayer.getCustomGui"); }
    @Override public void trigger(int id, Object... arguments) { throw XenoApiAdapters.unsupported("IPlayer.trigger"); }
    @Override public int getScreenWidth() { throw XenoApiAdapters.unsupported("IPlayer.getScreenWidth"); }
    @Override public int getScreenHeight() { throw XenoApiAdapters.unsupported("IPlayer.getScreenHeight"); }
    @Override public void openWebsite(String url) { throw XenoApiAdapters.unsupported("IPlayer.openWebsite"); }

    @Override
    public ServerPlayer getMCEntity() {
        throw XenoApiAdapters.unsupported("IPlayer.getMCEntity (raw handles are not exposed)");
    }
}
