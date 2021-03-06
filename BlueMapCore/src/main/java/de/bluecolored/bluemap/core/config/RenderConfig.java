/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.bluecolored.bluemap.core.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;

public class RenderConfig {

	private File webRoot = new File("web");
	private boolean useCookies;
	private List<MapConfig> mapConfigs = new ArrayList<>();

	public RenderConfig(ConfigurationNode node) throws IOException {

		//webroot
		String webRootString = node.getNode("webroot").getString();
		if (webRootString == null) throw new IOException("Invalid configuration: Node webroot is not defined");
		webRoot = ConfigManager.toFolder(webRootString);
		
		//cookies
		useCookies = node.getNode("useCookies").getBoolean(true);
		
		//maps
		mapConfigs = new ArrayList<>();
		for (ConfigurationNode mapConfigNode : node.getNode("maps").getChildrenList()) {
			mapConfigs.add(new MapConfig(mapConfigNode));
		}
		
	}

	public File getWebRoot() {
		if (!webRoot.exists()) webRoot.mkdirs();
		return webRoot;
	}
	
	public boolean isUseCookies() {
		return useCookies;
	}
	
	public List<MapConfig> getMapConfigs(){
		return mapConfigs;
	}
	
	
}
