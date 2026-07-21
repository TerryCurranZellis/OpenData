/*
 *  Filename: TransformService.java
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Performs transformation of extracted data.
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 21 Jul 2026
 */
public final class TransformService {

    /**
     * Cleans and prepares records.
     *
     * @param records input records
     * @return transformed records
     */
    public List<Map<String, String>> transform(
            List<Map<String, String>> records) {

        return records.stream()
                .map(this::cleanRecord)
                .collect(Collectors.toList());
    }

    private Map<String, String> cleanRecord(
            Map<String, String> record) {

        record.replaceAll(
                (key, value)
                -> value == null
                        ? ""
                        : value.trim());

        return record;
    }
}
