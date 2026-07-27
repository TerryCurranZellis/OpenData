/*
 * Filename: DiscoveredLinkSelector.java
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

import com.towermarsh.opendata.exception.DiscoveryException;
import java.util.List;

/**
 * Selects one download link from a set of discovered candidates.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public interface DiscoveredLinkSelector {

    /**
     * Selects one candidate.
     *
     * @param candidates discovered candidates
     * @param preferredTerms terms used to favour the intended dataset
     * @return selected candidate
     * @throws DiscoveryException if selection is unsafe or impossible
     */
    DiscoveredLink select(
            List<DiscoveredLink> candidates,
            List<String> preferredTerms) throws DiscoveryException;
}
