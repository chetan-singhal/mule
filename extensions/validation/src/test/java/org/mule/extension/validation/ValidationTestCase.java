/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.config.i18n.Message;
import org.mule.extension.validation.api.ValidationException;
import org.mule.extension.validation.internal.ValidationExtension;
import org.mule.extension.validation.internal.ValidationMessages;
import org.mule.functional.junit4.ExtensionFunctionalTestCase;
import org.mule.functional.junit4.FlowRunner;
import org.mule.runtime.core.util.ExceptionUtils;

abstract class ValidationTestCase extends ExtensionFunctionalTestCase
{

    static final String VALID_URL = "http://localhost:8080";
    static final String INVALID_URL = "here";

    static final String VALID_EMAIL = "mariano.gonzalez@mulesoft.com";
    static final String INVALID_EMAIL = "@mulesoft.com";

    protected ValidationMessages messages;

    @Override
    protected Class<?>[] getAnnotatedExtensionClasses()
    {
        return new Class<?>[] {ValidationExtension.class};
    }

    @Override
    protected void doSetUp() throws Exception
    {
        messages = new ValidationMessages();
    }

    protected void assertValid(FlowRunner runner) throws MuleException, Exception
    {
        assertThat(runner.run().getMessage().getExceptionPayload(), is(nullValue()));
        runner.reset();
    }

    protected void assertInvalid(FlowRunner runner, Message expectedMessage) throws Exception
    {
        Exception e = runner.runExpectingException();

        Throwable rootCause = ExceptionUtils.getRootCause(e);
        assertThat(rootCause, is(instanceOf(ValidationException.class)));
        assertThat(rootCause.getMessage(), is(expectedMessage.getMessage()));
        // assert that all placeholders were replaced in message
        assertThat(rootCause.getMessage(), not(containsString("${")));

        runner.reset();
    }
}
