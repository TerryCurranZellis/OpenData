/*
 * Filename: DownloadResult.java
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
package com.towermarsh.opendata.download;

import com.towermarsh.opendata.model.DataFile;

/**
 * Represents the result of a download operation.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class DownloadResult {

    private final boolean successful;
    private final DataFile file;
    private final String message;

    public DownloadResult(
            boolean successful,
            DataFile file,
            String message) {

        this.successful = successful;
        this.file = file;
        this.message = message;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public DataFile getFile() {
        return file;
    }

    public String getMessage() {
        return message;
    }
}
