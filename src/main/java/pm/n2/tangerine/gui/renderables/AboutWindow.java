package pm.n2.tangerine.gui.renderables;

import imgui.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Holder;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import pm.n2.tangerine.gui.TangerineRenderable;

public class AboutWindow extends TangerineRenderable {
	private final MusicSound stal = new MusicSound(
		Holder.createDirect(SoundEvents.MUSIC_DISC_STAL),
		0,
		0,
		true);
	private boolean lastEnabled = false;


	public AboutWindow() {
		super("AboutWindow", false);
	}

	private void onOpen() {
		MinecraftClient.getInstance().getMusicTracker().play(stal);
	}

	private void onClose() {
		var musicTracker = MinecraftClient.getInstance().getMusicTracker();
		if (musicTracker.isPlayingMusic(stal)) musicTracker.stopPlaying();
	}

	@Override
	public void draw() {
		var enabled = new ImBoolean(this.enabled);

		if (ImGui.begin("About Tangerine", enabled)) {
			ImGui.textUnformatted("Tangerine, a NotNet project");
			ImGui.textUnformatted("Powered by Cauldron and imgui-quilt");
			ImGui.textUnformatted("(c) NotNite, adryd, Cynosphere, and Murmiration, 2022");

			if (ImGui.button("Open source code")) {
				Util.getOperatingSystem().open("https://github.com/n2pm/tangerine");
			}

			if (ImGui.button("Crash game")) {
				throw new RuntimeException("She strogan my beef til im off");
			}
		}

		ImGui.end();

		this.enabled = enabled.get();

		if (this.enabled != this.lastEnabled) {
			if (this.enabled) {
				this.onOpen();
			} else {
				this.onClose();
			}
		}

		this.lastEnabled = this.enabled;
	}
}
