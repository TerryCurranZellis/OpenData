/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

/**
 * Encrypts and decrypts configuration secrets stored in properties.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public interface ConfigurationPasswordCipher {

    /**
     * Encrypts one plain-text value.
     *
     * @param plainText plain-text value
     * @return encrypted value ready for storage
     */
    String encrypt(String plainText);

    /**
     * Decrypts one stored value.
     *
     * @param storedValue stored value, encrypted or plain text
     * @return plain-text value
     */
    String decrypt(String storedValue);

    /**
     * Checks whether one stored value is encrypted.
     *
     * @param storedValue stored value
     * @return {@code true} when encrypted
     */
    boolean isEncrypted(String storedValue);
}
