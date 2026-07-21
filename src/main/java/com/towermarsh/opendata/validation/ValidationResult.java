/*
 *  Filename: NewClass.java
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
package com.towermarsh.opendata.validation;

import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a validation operation.
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 21 Jul 2026
 */
public final class ValidationResult {

    private final boolean valid;

    private final long recordsChecked;

    private final long recordsRejected;

    private final List<String> errors;

    /**
     * Creates a validation result.
     *
     * @param valid whether validation passed
     * @param recordsChecked number of records checked
     * @param recordsRejected number rejected
     * @param errors validation messages
     */
    public ValidationResult(
            boolean valid,
            long recordsChecked,
            long recordsRejected,
            List<String> errors) {

        this.valid = valid;

        this.recordsChecked = recordsChecked;

        this.recordsRejected = recordsRejected;

        this.errors
                = Collections.unmodifiableList(errors);
    }

    public boolean isValid() {
        return valid;
    }

    public long getRecordsChecked() {
        return recordsChecked;
    }

    public long getRecordsRejected() {
        return recordsRejected;
    }

    public List<String> getErrors() {
        return errors;
    }
}
