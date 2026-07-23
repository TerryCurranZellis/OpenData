/*
 * Filename: LinkDiscoveryDefinition.java
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

package com.towermarsh.opendata.config.model;

import java.util.Objects;

/**
 * Rules for finding a downloadable file link on an HTML landing page.
 *
 * @param cssSelector CSS selector used to select candidate anchors
 * @param hrefPattern regular expression applied to the href
 * @param textPattern optional regular expression applied to link text
 * @param selectLastMatchingLink whether the final matching link is selected
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public record LinkDiscoveryDefinition(
        String cssSelector,
        String hrefPattern,
        String textPattern,
        boolean selectLastMatchingLink) {

    public LinkDiscoveryDefinition {
        Objects.requireNonNull(cssSelector, "cssSelector");
        Objects.requireNonNull(hrefPattern, "hrefPattern");
        textPattern = textPattern == null ? "" : textPattern;
    }
}
