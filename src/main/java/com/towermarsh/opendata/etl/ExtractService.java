/*
 *  Filename: ExtractService.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.etl;

import com.towermarsh.opendata.download.DataDownloader;
import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.model.DataFile;
import com.towermarsh.opendata.parser.DataParser;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Extracts data from external OpenData sources.
 *
 * <p>
 * The extract phase downloads a dataset and converts it into an internal record
 * structure.</p>
 *
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 21 Jul 2026
 */
public final class ExtractService {

    private final DataDownloader downloader;
    private final DataParser parser;

    /**
     * Creates an extract service.
     *
     * @param downloader file downloader
     * @param parser data parser
     */
    public ExtractService(
            DataDownloader downloader,
            DataParser parser) {

        this.downloader = downloader;
        this.parser = parser;
    }

    /**
     * Extracts records from a remote dataset.
     *
     * @param source source URL
     * @param destination local file
     * @return extracted records
     *
     * @throws DownloadException download failure
     * @throws ImportException parsing failure
     */
    public List<Map<String, String>> extract(
            URI source,
            Path destination)
            throws DownloadException, ImportException {

        DataFile file
                = downloader.download(
                        source,
                        destination);

        return parser.parse(
                file.getFilePath());
    }
}
