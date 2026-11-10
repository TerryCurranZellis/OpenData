/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Example dry-run policy and transactional persistence stage.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link ExampleLoad} &mdash; Plugin-specific load facade.</li>
 * <li>{@link ExampleLoader} &mdash; Database persistence implementation.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link ExampleLoadResult} &mdash; Counts written and skipped records.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 4.1.0
 */
package com.towermarsh.opendata.plugin.example.load;
