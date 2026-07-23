/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.discovery;

import com.towermarsh.opendata.exception.DiscoveryException;
import java.util.List;

/**
 * Finds dataset links on HTML pages.
 */
public interface HtmlLinkDiscoverer {

    /**
     * Discovers all links accepted by the request.
     *
     * @param request discovery request
     * @return accepted links in document order
     * @throws DiscoveryException if the page cannot be obtained or parsed
     */
    List<DiscoveredLink> discover(LinkDiscoveryRequest request)
            throws DiscoveryException;
}
