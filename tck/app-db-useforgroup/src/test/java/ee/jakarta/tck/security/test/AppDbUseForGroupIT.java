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
public class AppDbUseForGroupIT extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return mavenWar();
    }

    /**
     * Migrated from old-tck idstore/database/useforgroup#testAnnotationDBIDStore_useforgroup.
     *
     * Three stores: DB (PROVIDE_GROUPS only, prio 100), IdentityStore1 (both
     * validation types, prio 1000) and IdentityStore2 (PROVIDE_GROUPS only,
     * prio 200). On a successful validation by IdentityStore1, all three
     * group-providing stores must contribute to the resulting group set.
     */
    @Test
    public void testAnnotationDBIDStore_useforgroup_tom() {
        String response = readFromServer("/ServletForDatabaseIDStore?user=tom&pwd=secret1");

        assertTrue(
                response.contains("ValidateResultStatus=VALID"), "Expected VALID status.\n" + response);
        assertTrue(
                response.contains("web username: tom"), "Expected web username tom.\n" + response);
        assertTrue(
                response.contains("Administrator1") && response.contains("Manager1"), "Expected groups from IdentityStore1.\n" + response);
        assertTrue(
                response.contains("Administrator") && response.contains("Manager"), "Expected groups from DB store.\n" + response);
        assertTrue(
                response.contains("Administrator2") && response.contains("Manager2"), "Expected groups from IdentityStore2.\n" + response);
    }

    @Test
    public void testAnnotationDBIDStore_useforgroup_emma() {
        String response = readFromServer("/ServletForDatabaseIDStore?user=emma&pwd=secret12");

        assertTrue(
                response.contains("ValidateResultStatus=VALID"), "Expected VALID status.\n" + response);
        assertTrue(
                response.contains("web username: emma"), "Expected web username emma.\n" + response);
        assertTrue(
                response.contains("Administrator1") && response.contains("Employee1"), "Expected groups from IdentityStore1.\n" + response);
        assertTrue(
                response.contains("Administrator2") && response.contains("Employee2"), "Expected groups from IdentityStore2.\n" + response);
    }

}
