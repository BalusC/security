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

import java.io.IOException;

import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class AppHamCustomFormBaseIT extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return mavenWar();
    }

    /**
     * Migrated from old-tck ham/customform/base#testCustomFormHAMValidateRequest.
     */
    @Test
    public void testCustomFormHAMValidateRequest() throws IOException {
        HtmlPage loginPage = pageFromServer("/servlet");

        assertTrue(
                loginPage.getWebResponse().getContentAsString().contains("Login"), "Expected to receive the login page.\n" + loginPage.getWebResponse().getContentAsString());

        HtmlForm form = loginPage.getForms().get(0);
        form.getInputByName("form:username").setValueAttribute("reza");
        form.getInputByName("form:password").setValueAttribute("secret1");

        HtmlPage page = form.getInputByValue("Login").click();
        String response = page.getWebResponse().getContentAsString();

        assertTrue(
                response.contains("The user principal is: reza"), "Expected the protected resource to identify the caller as reza.\n" + response);
        assertTrue(
                response.contains("getRemoteUser(): reza"), "Expected getRemoteUser() to return reza.\n" + response);
        assertTrue(response.contains("isUserInRole(\"foo\"): !true!"));
        assertTrue(response.contains("isUserInRole(\"bar\"): !true!"));
        assertTrue(response.contains("isUserInRole(\"kaz\"): !false!"));

        // Re-access the protected resource — the FORM session keeps the caller authenticated.
        String reaccess = readFromServer("/servlet");
        assertTrue(
                reaccess.contains("The user principal is: reza"), "Expected to remain authenticated as reza on subsequent request.\n" + reaccess);
    }

    /**
     * Migrated from old-tck ham/customform/base#testLoginToContinueuseRedirectToLogin:
     * with useForwardToLogin = false the request is redirected (not forwarded)
     * to /login.jsf, so the resulting URL should reflect the login page path.
     */
    @Test
    public void testLoginToContinueUseRedirectToLogin() {
        HtmlPage loginPage = pageFromServer("/servlet");

        assertTrue(
                loginPage.getUrl().toString().contains("/login.jsf"), "Expected the URL to be redirected to /login.jsf.\n"
                + loginPage.getUrl().toString());
        assertTrue(
                loginPage.getWebResponse().getContentAsString().contains("Login"), "Expected the login page content.\n" + loginPage.getWebResponse().getContentAsString());
    }

    /**
     * Migrated from old-tck ham/customform/base#testCustomFormLoginToContinueErrorPage.
     */
    @Test
    public void testCustomFormLoginToContinueErrorPage() throws IOException {
        HtmlPage loginPage = pageFromServer("/servlet");

        HtmlForm form = loginPage.getForms().get(0);
        form.getInputByName("form:username").setValueAttribute("reza");
        form.getInputByName("form:password").setValueAttribute("wrongpwd");

        HtmlPage page = form.getInputByValue("Login").click();
        String response = page.getWebResponse().getContentAsString();

        assertTrue(
                response.contains("The user principal is: null"), "Expected an unauthenticated error page (caller principal null).\n" + response);
        assertTrue(
                page.getUrl().toString().contains("/login-error-servlet"), "Expected to land on /login-error-servlet.\n" + page.getUrl().toString());
    }

    /**
     * Migrated from old-tck ham/customform/base#testCustomFormHAMHasCorrectQualifier.
     */
    @Test
    public void testCustomFormHAMHasCorrectQualifier() throws IOException {
        HtmlPage loginPage = pageFromServer("/servlet2");

        HtmlForm form = loginPage.getForms().get(0);
        form.getInputByName("form:username").setValueAttribute("reza");
        form.getInputByName("form:password").setValueAttribute("secret1");

        HtmlPage page = form.getInputByValue("Login").click();
        String response = page.getWebResponse().getContentAsString();

        assertTrue(
                response.contains("Have qualifier @CustomFormAuthenticationMechanism: true"), "Expected the Custom-FORM HAM bean to carry @CustomFormAuthenticationMechanism.\n" + response);
        assertTrue(
                response.contains("Have scope @ApplicationScoped: true"), "Expected the Custom-FORM HAM bean to be @ApplicationScoped.\n" + response);
    }

}
