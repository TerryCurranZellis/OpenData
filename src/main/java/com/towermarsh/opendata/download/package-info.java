/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Download abstractions and HTTP downloader implementations.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link DownloadResult} &mdash; Represents the result of a download operation.</li>
 * <li>{@link HttpDataDownloader} &mdash; Streaming HTTP implementation of {@link DataDownloader}.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link HttpDownloadOptions} &mdash; Immutable HTTP download settings.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link DataDownloader} &mdash; Defines the contract for downloading OpenData files.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.download;
