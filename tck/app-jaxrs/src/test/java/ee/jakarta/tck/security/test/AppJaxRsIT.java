/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(ArquillianExtension.class)
public class AppJaxRsIT extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        try {
            return mavenWar();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    @Test
    public void testAuthenticated() {
        String response = readFromServer("/rest/resource/callerName?name=reza&password=secret1");

        assertTrue(
            response.contains("reza"),
            "Should be authenticated as user reza but was not");
    }

    @Test
    public void testNotAuthenticated() {
        String response = readFromServer("/rest/resource/callerName");

        assertFalse(
            response.contains("reza"),
            "Should not be authenticated as user reza but was");
    }

    @Test
    public void testHasRoleFoo() {
        String response = readFromServer("/rest/resource/hasRoleFoo?name=reza&password=secret1");

        assertTrue(
            response.contains("true"),
            "Should be in role foo, but was not");
    }

    @Test
    public void testNotHasRoleFoo() {
        String response = readFromServer("/rest/resource/hasRoleFoo");

        assertTrue(
            response.contains("false"),
            "Should not be in role foo, but was");
    }

    @Test
    public void testNotHasRoleKaz1() {
        String response = readFromServer("/rest/resource/hasRoleKaz?name=reza&password=secret1");

        assertFalse(
            response.contains("true"),
            "Should not be in role kaz, but was");
    }

    @Test
    public void testNotHasRoleKaz2() {
        String response = readFromServer("/rest/resource/hasRoleKaz");

        assertFalse(
            response.contains("true"),
            "Should not be in role kaz, but was");
    }

    @Test
    public void testSayHi() {
        String response = readFromServer("/rest/protectedResource/sayHi?name=reza&password=secret1");

        assertTrue(
            response.contains("saying hi!"),
            "Endpoint should have been called, but was not");
    }

    @Test
    public void testNotSayHi() {
        String response = readFromServer("/rest/protectedResource/sayHi");

        assertFalse(
            response.contains("saying hi!"),
            "Endpoint should not have been called, but was");
    }

}
