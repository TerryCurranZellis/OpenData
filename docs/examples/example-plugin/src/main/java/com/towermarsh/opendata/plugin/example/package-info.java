/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Documentation-only example provider, including a workflow, typed configuration, an HTTP client, a transformer and persistence placeholders.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link ExamplePlugin} &mdash; Illustrates the {@code OpenDataPlugin} entry point.</li>
 * <li>{@link ExampleRepository} &mdash; Shows the persistence boundary and its {@code SaveResult} record.</li>
 * <li>{@link ExampleSourceClient} &mdash; Demonstrates a bounded HTTP source request.</li>
 * <li>{@link ExampleTransformer} &mdash; Transforms an example response.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link ExamplePluginConfiguration} &mdash; Validated source settings.</li>
 * <li>{@link ExampleRecord} &mdash; Immutable transformed input.</li>
 * </ul>
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
package com.towermarsh.opendata.plugin.example;
