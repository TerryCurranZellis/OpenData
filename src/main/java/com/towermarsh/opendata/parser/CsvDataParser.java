/*
 *  Filename: CSVParser.java
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
package com.towermarsh.opendata.parser;

import com.towermarsh.opendata.exception.ImportException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Parser for comma separated files.
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 21 Jul 2026
 */
public final class CsvDataParser
        implements DataParser {

    @Override
    public List<Map<String, String>> parse(
            Path file)
            throws ImportException {

        try {

            List<String> lines
                    = Files.readAllLines(file);

            if (lines.isEmpty()) {
                return List.of();
            }

            String[] headers
                    = lines.get(0)
                            .split(",");

            return lines.stream()
                    .skip(1)
                    .map(line
                            -> createRecord(
                            headers,
                            line.split(",")))
                    .collect(Collectors.toList());

        } catch (IOException ex) {

            throw new ImportException(
                    "Unable to parse CSV file",
                    ex);
        }
    }

    private Map<String, String> createRecord(
            String[] headers,
            String[] values) {

        Map<String, String> record
                = new LinkedHashMap<>();

        for (int i = 0;
                i < headers.length;
                i++) {

            String value
                    = i < values.length
                            ? values[i]
                            : "";

            record.put(
                    headers[i],
                    value);
        }

        return record;
    }
}
