package com.redsponge.dbf.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.redsponge.dbf.DashniBossFight;

/** Launches the desktop (LWJGL) application. */
public class DesktopLauncher {
	public static void main(String[] args) {
		createApplication();
	}

	private static LwjglApplication createApplication() {
		return new LwjglApplication(new DashniBossFight(), getDefaultConfiguration());
	}

	private static LwjglApplicationConfiguration getDefaultConfiguration() {
		LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
		configuration.title = "DashniBossFight";
		configuration.width = 640;
		configuration.height = 480;
		configuration.audioDeviceBufferCount = 4096 * 10;
		for (int size : new int[] { 128, 64, 32 }) {
			configuration.addIcon(size + ".png", FileType.Internal);
		}
		return configuration;
	}
}