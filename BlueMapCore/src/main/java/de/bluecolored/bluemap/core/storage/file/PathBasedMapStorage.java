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
package de.bluecolored.bluemap.core.storage.file;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.bluecolored.bluemap.core.storage.compression.Compression;
import de.bluecolored.bluemap.core.storage.GridStorage;
import de.bluecolored.bluemap.core.storage.MapStorage;
import de.bluecolored.bluemap.core.storage.SingleItemStorage;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.util.stream.Stream;

public abstract class PathBasedMapStorage implements MapStorage {

    public static final Path SETTINGS_PATH = Path.of("settings.json");
    public static final Path TEXTURES_PATH = Path.of("textures.json");
    public static final Path RENDER_STATE_PATH = Path.of(".rstate");
    public static final Path MARKERS_PATH = Path.of("live", "markers.json");
    public static final Path PLAYERS_PATH = Path.of("live", "players.json");

    private final GridStorage hiresGridStorage;
    private final LoadingCache<Integer, GridStorage> lowresGridStorages;

    public PathBasedMapStorage(Compression compression, String hiresSuffix, String lowresSuffix) {
        this.hiresGridStorage = new PathBasedGridStorage(
                this,
                Path.of("tiles", "0"),
                hiresSuffix + compression.getFileSuffix(),
                compression
        );

        this.lowresGridStorages = Caffeine.newBuilder().build(lod -> new PathBasedGridStorage(
                this,
                Path.of("tiles", String.valueOf(lod)),
                lowresSuffix,
                Compression.NONE
        ));
    }

    @Override
    public GridStorage hiresTiles() {
        return hiresGridStorage;
    }

    @Override
    public GridStorage lowresTiles(int lod) {
        return lowresGridStorages.get(lod);
    }

    public Path getAssetPath(String name) {
        String[] parts = MapStorage.escapeAssetName(name)
                .split("/");
        return Path.of("assets", parts);
    }

    @Override
    public SingleItemStorage asset(String name) {
        return file(getAssetPath(name), Compression.NONE);
    }

    @Override
    public SingleItemStorage renderState() {
        return file(RENDER_STATE_PATH, Compression.NONE);
    }

    @Override
    public SingleItemStorage settings() {
        return file(SETTINGS_PATH, Compression.NONE);
    }

    @Override
    public SingleItemStorage textures() {
        return file(TEXTURES_PATH, Compression.NONE);
    }

    @Override
    public SingleItemStorage markers() {
        return file(MARKERS_PATH, Compression.NONE);
    }

    @Override
    public SingleItemStorage players() {
        return file(PLAYERS_PATH, Compression.NONE);
    }

    /**
     * Returns a {@link SingleItemStorage} for a file with the given path and compression.
     * The file does not have to actually exist.
     */
    public abstract SingleItemStorage file(Path file, Compression compression);

    /**
     * Returns a stream with all file-paths of existing files at or below the given path.
     * (Including files in potential sub-folders)<br>
     * Basically, this method should mimic the functionality of
     * {@link java.nio.file.Files#walk(Path, FileVisitOption...)}
     */
    public abstract Stream<Path> files(Path path) throws IOException;

}
