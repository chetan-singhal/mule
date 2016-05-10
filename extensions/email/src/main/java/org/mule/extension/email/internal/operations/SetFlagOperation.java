/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.email.internal.operations;

import static java.lang.String.format;
import static org.mule.extension.email.internal.util.EmailUtils.getAttributesFromMessage;
import org.mule.extension.email.internal.EmailAttributes;
import org.mule.extension.email.internal.exception.EmailException;
import org.mule.runtime.api.message.MuleMessage;

import java.util.List;
import java.util.Optional;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Represents the set flag operation.
 *
 * @since 4.0
 */
public class SetFlagOperation
{

    /**
     * Sets the specified {@code flag} into an email or {@link List} of emails
     * that are being carried in the incoming {@link MuleMessage}
     * <p>
     * if no email(s) are found in the incoming {@link MuleMessage} the operation
     * will fetch the email for the specified {@code emailNumber}.
     * <p>
     * If no email(s) are found in the {@link MuleMessage} and no {@code emailNumber} is specified.
     * the operation will fail.
     *
     * @param muleMessage the incoming {@link MuleMessage}.
     * @param folder      the configured folder.
     * @param emailNumber the optional number of the email to be marked. for default the email is taken from the incoming {@link MuleMessage}.
     * @param flag        the flag to be setted.
     * @param expunge     if true, all mails marked as deleted will be erased from the folder.
     */
    public void set(MuleMessage muleMessage, Folder folder, Integer emailNumber, Flag flag, boolean expunge)
    {
        Object payload = muleMessage.getPayload();
        if (payload instanceof List)
        {
            for (Object o : (List) payload)
            {
                if (o instanceof MuleMessage)
                {
                    setFlag((MuleMessage) o, folder, emailNumber, flag);
                }
                else
                {
                    throw new EmailException("Cannot perform operation for the incoming payload");
                }
            }
        }
        else
        {
            setFlag(muleMessage, folder, emailNumber, flag);
        }

        try
        {
            folder.close(expunge);
        }
        catch (MessagingException e)
        {
            throw new EmailException("Error while closing folder " + folder.getName());
        }

    }

    /**
     * Sets the specified {@code flag} into an email.
     * <p>
     * The email is taken from the incoming {@link MuleMessage}, if no email is found in the {@link MuleMessage}
     * is going to be fetched from the folder by the specified {@code emailNumber}. If no email was found in the  {@link MuleMessage}
     * and the {@code emailNumber} was not specified the operation will fail.
     *
     * @param muleMessage the incoming {@link MuleMessage}.
     * @param folder      the configured folder.
     * @param emailNumber the optional number of the email to be marked. for default the email is taken from the incoming {@link MuleMessage}.
     * @param flag        the flag to be setted.
     */
    private void setFlag(MuleMessage muleMessage, Folder folder, Integer emailNumber, Flag flag)
    {
        Optional<EmailAttributes> attributes = getAttributesFromMessage(muleMessage);

        if (attributes.isPresent())
        {
            emailNumber = attributes.get().getNumber();
        }
        else
        {
            if (emailNumber == null)
            {
                throw new EmailException("No emailId specified for the operation. Expecting email attributes in the incoming mule message or an explicit emailId value");
            }
        }

        try
        {
            Message message = folder.getMessage(emailNumber);
            message.setFlag(flag, true);
        }
        catch (MessagingException e)
        {
            throw new EmailException(format("Error while fetching email id [%s] ", emailNumber), e);
        }
    }
}
