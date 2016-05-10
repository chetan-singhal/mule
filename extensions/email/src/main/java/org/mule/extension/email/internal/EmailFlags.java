/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.email.internal;

/**
 * The {@link EmailFlags} class represents the set of flags on a Message. Flags
 * are composed of predefined system flags that most folder
 * implementations are expected to support.
 *
 * @since 4.0
 */
public class EmailFlags
{

    public EmailFlags(Boolean answered, Boolean deleted, Boolean draft, Boolean recent, Boolean seen)
    {
        this.answered = answered;
        this.deleted = deleted;
        this.draft = draft;
        this.recent = recent;
        this.seen = seen;
    }

    private Boolean answered;
    private Boolean deleted;
    private Boolean draft;
    private Boolean recent;
    private Boolean seen;

    /**
     * @return if this message has been answered.
     */
    public Boolean answered()
    {
        return answered;
    }

    /**
     * @return if this message has been deleted.
     */
    public Boolean deleted()
    {
        return deleted;
    }

    /**
     * @return if this message is a draft.
     */
    public Boolean draft()
    {
        return draft;
    }

    /**
     * @return if this message is recent. Folder implementations set this flag
     * to indicate that this message is new to this folder, that is,
     * it has arrived since the last time this folder was opened.
     */
    public Boolean recent()
    {
        return recent;
    }

    /**
     * @return if this message has been seen. This flag is implicitly set by the
     * implementation when the the email content is returned
     * to the client in some form.
     */
    public Boolean seen()
    {
        return seen;
    }

}
