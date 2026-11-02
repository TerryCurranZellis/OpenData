/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.neso;

import java.nio.file.Path;

/**
 * One CSV resource listed by the NESO catalogue and the local copy of it.
 *
 * @param name resource name
 * @param url HTTPS download URL
 * @param file local working copy, or {@code null} before download
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public record NesoResource(String name, String url, Path file) {

    /**
     * Returns a copy bound to a downloaded file.
     *
     * @param downloaded local file
     * @return downloaded resource
     */
    public NesoResource withFile(final Path downloaded) {
        return new NesoResource(name, url, downloaded);
    }
}
