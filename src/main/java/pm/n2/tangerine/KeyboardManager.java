package pm.n2.tangerine;

import org.quiltmc.qsl.base.api.event.Event;

import java.util.ArrayList;

public class KeyboardManager {
	public final Event<KeyPress> KEY_PRESS = Event.create(KeyPress.class, listeners -> key -> {
		for (KeyPress listener : listeners) listener.onKeyPress(key);
	});

	private final ArrayList<Integer> pressedKeys = new ArrayList<>();

	public void keyPress(int key) {
		if (!pressedKeys.contains(key)) {
			pressedKeys.add(key);
			KEY_PRESS.invoker().onKeyPress(key);
		}
	}

	public void keyRelease(int key) {
		if (pressedKeys.contains(key)) pressedKeys.remove((Integer) key);
	}

	public boolean isKeyPressed(int key) {
		return pressedKeys.contains(key);
	}

	public interface KeyPress {
		void onKeyPress(int key);
	}
}
