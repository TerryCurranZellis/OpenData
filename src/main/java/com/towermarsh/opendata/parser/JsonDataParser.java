/*
 *  Filename: JsonParser.java
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towermarsh.opendata.exception.ImportException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Parser for JSON datasets.
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 21 Jul 2026
 */
public final class JsonDataParser
        implements DataParser {

    private final ObjectMapper mapper;

    public JsonDataParser() {

        mapper
                = new ObjectMapper();
    }

    @Override
    public List<Map<String, String>> parse(
            Path file)
            throws ImportException {

        try {

            return mapper.readValue(
                    file.toFile(),
                    new TypeReference<
                        List<Map<String, String>>>() {
            });

        } catch (IOException ex) {

            throw new ImportException(
                    "Unable to parse JSON file",
                    ex);
        }
    }
}
