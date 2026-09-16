/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * HTML link discovery models and selection strategies.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link HighestScoringLinkSelector} &mdash; Selects the candidate that best matches configured preferred terms.</li>
 * <li>{@link JsoupHtmlLinkDiscoverer} &mdash; Jsoup implementation of HTML link discovery.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link DiscoveredLink} &mdash; A resolved hyperlink discovered on a source web page.</li>
 * <li>{@link LinkDiscoveryRequest} &mdash; Immutable criteria used to discover candidate dataset links.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link DiscoveredLinkSelector} &mdash; Selects one download link from a set of discovered candidates.</li>
 * <li>{@link HtmlLinkDiscoverer} &mdash; Finds dataset links on HTML pages.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.discovery;
