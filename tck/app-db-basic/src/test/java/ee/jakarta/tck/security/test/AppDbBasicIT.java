/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package ee.jakarta.tck.security.test;

import static ee.jakarta.tck.security.test.ShrinkWrap.mavenWar;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class AppDbBasicIT extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return mavenWar();
    }

    /**
     * Migrated from old-tck idstore/database/basic#testAnnotationDBIDStore_Basic.
     *
     * Default attributes of {@code @DatabaseIdentityStoreDefinition} are
     * exercised: callerQuery returns the password column, groupsQuery returns
     * the group rows, hashAlgorithm = plaintext. Three sub-scenarios:
     * <ol>
     *   <li>Valid user / valid password -> VALID with Administrator+Manager
     *   <li>Valid user / wrong password -> INVALID with no groups
     *   <li>Unknown user -> INVALID with no groups
     * </ol>
     */
    @Test
    public void testAnnotationDBIDStore_Basic_valid() {
        String response = readFromServer("/ServletForDatabaseIDStore?user=tom&pwd=secret1");

        assertTrue(
                response.contains("ValidateResultStatus=VALID"), "Expected VALID status.\n" + response);
        assertTrue(response.contains("Administrator"), "Expected Administrator group.\n" + response);
        assertTrue(response.contains("Manager"), "Expected Manager group.\n" + response);
        assertTrue(response.contains("ValidateCallerDN=null"), "Expected ValidateCallerDN=null.\n" + response);
        assertTrue(response.contains("web username: tom"), "Expected web username tom.\n" + response);
    }

    @Test
    public void testAnnotationDBIDStore_Basic_pwdInvalid() {
        String response = readFromServer("/ServletForDatabaseIDStore?user=tom&pwd=invalid_pwd");

        assertTrue(
                response.contains("ValidateResultStatus=INVALID"), "Expected INVALID status.\n" + response);
        assertTrue(
                response.contains("ValidateResultGroups=[]"), "Expected empty group set.\n" + response);
    }

    @Test
    public void testAnnotationDBIDStore_Basic_userInvalid() {
        String response = readFromServer("/ServletForDatabaseIDStore?user=invalid_user&pwd=secret1");

        assertTrue(
                response.contains("ValidateResultStatus=INVALID"), "Expected INVALID status.\n" + response);
        assertTrue(
                response.contains("ValidateResultGroups=[]"), "Expected empty group set.\n" + response);
    }

}
