/*
 * Filename: DiscoveryException.java
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
package com.towermarsh.opendata.exception;

/**
 * Indicates that a dataset link could not be discovered or selected safely.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public class DiscoveryException extends OpenDataException {

    /**
     * Creates a new discovery exception.
     *
     * @param message the detail message
     */
    public DiscoveryException(String message) {
        super(message);
    }

    /**
     * Creates a new discovery exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public DiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
