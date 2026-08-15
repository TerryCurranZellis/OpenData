/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Download strategy coordination for direct and HTML-discovered files.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link DirectHttpDownloadStrategy} &mdash; Streams a remote HTTP resource to a local file.</li>
 * <li>{@link HtmlLinkDiscoveryStrategy} &mdash; Downloads an HTML landing page, discovers a matching file link and streams the resolved resource to disk.</li>
 * <li>{@link HtmlLinkResolver} &mdash; Resolves a downloadable link from an already-downloaded HTML document.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link ResolvedDownload} &mdash; Result of resolving and downloading one remote resource.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.download.strategy;
