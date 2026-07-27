/*
 * Filename: DiscoveredLink.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.discovery;

import java.net.URI;
import java.util.Locale;
import java.util.Objects;

/**
 * A resolved hyperlink discovered on a source web page.
 *
 * @param pageUri page on which the link was found
 * @param targetUri absolute target URI
 * @param linkText visible anchor text
 * @param title anchor title attribute
 * @param fileName final path segment, when available
 * @param extension lower-case filename extension without a leading dot
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record DiscoveredLink(
        URI pageUri,
        URI targetUri,
        String linkText,
        String title,
        String fileName,
        String extension) {

    /** Validates and normalises record components. */

    public DiscoveredLink {
        Objects.requireNonNull(pageUri, "pageUri");
        Objects.requireNonNull(targetUri, "targetUri");
        linkText = Objects.requireNonNullElse(linkText, "").trim();
        title = Objects.requireNonNullElse(title, "").trim();
        fileName = Objects.requireNonNullElse(fileName, "").trim();
        extension = Objects.requireNonNullElse(extension, "")
                .replaceFirst("^\\.", "")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Returns searchable lower-case text assembled from the link metadata.
     *
     * @return combined searchable text
     */
    public String searchableText() {
        return String.join(" ", linkText, title, fileName, targetUri.toString())
                .toLowerCase(Locale.ROOT);
    }
}
