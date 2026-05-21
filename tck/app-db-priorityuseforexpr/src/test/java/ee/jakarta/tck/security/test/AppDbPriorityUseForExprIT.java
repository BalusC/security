/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
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
public class AppDbPriorityUseForExprIT extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return mavenWar();
    }

    /**
     * Migrated from old-tck idstore/database/priorityuseforexpr#testAnnotationDBIDStore_priorityuseforexpr.
     *
     * The DB store is annotated with {@code useFor = VALIDATE} but
     * {@code useForExpression = "#{configBean.useforProvideGroup}"} overrides
     * that at runtime to {@code PROVIDE_GROUPS}. Validation is therefore done
     * by the in-mem IdentityStore1; the DB store contributes groups for users
     * present in the database.
     */
    @Test
    public void testAnnotationDBIDStore_priorityuseforexpr_tom() {
        String response = readFromServer("/ServletForDatabaseIDStore?user=tom&pwd=secret1");

        assertTrue(
                response.contains("ValidateResultStatus=VALID"), "Expected VALID status from in-mem store.\n" + response);
        assertTrue(
                response.contains("web username: tom"), "Expected web username tom.\n" + response);
        assertTrue(
                response.contains("Administrator1") && response.contains("Manager1"), "Expected groups from IdentityStore1.\n" + response);
        assertTrue(
                response.contains("Administrator") && response.contains("Manager"), "Expected groups from DB store.\n" + response);
    }

    @Test
    public void testAnnotationDBIDStore_priorityuseforexpr_emma() {
        // emma/secret2 only matches the DB store. But the DB store is
        // PROVIDE_GROUPS only -- it cannot validate the credential, so the
        // overall result is INVALID with no groups.
        String response = readFromServer("/ServletForDatabaseIDStore?user=emma&pwd=secret2");

        assertTrue(
                response.contains("ValidateResultStatus=INVALID"), "Expected INVALID status (DB is PROVIDE_GROUPS only, in-mem rejects pwd).\n" + response);
        assertTrue(
                response.contains("ValidateResultGroups=[]"), "Expected empty group set.\n" + response);
    }

}
