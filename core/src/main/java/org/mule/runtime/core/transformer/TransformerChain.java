/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.transformer;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.api.transformer.Transformer;
import org.mule.runtime.core.api.transformer.TransformerException;
import org.mule.runtime.core.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A referencable chain of transformers that can be used as a single transformer
 */
public class TransformerChain extends AbstractMessageTransformer
{
    private List<Transformer> transformers;

    public TransformerChain(List<Transformer> transformers)
    {
        super();
        if (transformers.size() < 1)
        {
            throw new IllegalArgumentException("You must set at least one transformer");
        }
        this.transformers = new LinkedList<Transformer>(transformers);
    }

    public TransformerChain(Transformer... transformers)
    {
        this(Arrays.asList(transformers));
        this.name = generateTransformerName();
    }

    public TransformerChain(String name, List<Transformer> transformers)
    {
        this(transformers);
        this.name = name;
    }

    public TransformerChain(String name, Transformer... transformers)
    {
        this(name, Arrays.asList(transformers));
    }

    @Override
    public Object transformMessage(MuleEvent event, String outputEncoding) throws TransformerException
    {
        MuleMessage result = event.getMessage();
        Object temp = event.getMessage();
        Transformer lastTransformer = null;
        for (Iterator iterator = transformers.iterator(); iterator.hasNext();)
        {
            lastTransformer = (Transformer) iterator.next();
            temp = lastTransformer.transform(temp);
            if (temp instanceof MuleMessage)
            {
                result = (MuleMessage) temp;
            }
            else
            {
                result.setPayload(temp);
            }
        }
        if (lastTransformer != null && lastTransformer.getReturnDataType().getType().equals(MuleMessage.class))
        {
            return result;
        }
        else
        {
            return result.getPayload();
        }
    }

    @Override
    public void initialise() throws InitialisationException
    {
        for (Transformer transformer : transformers)
        {
            transformer.initialise();
        }
    }

    @Override
    public void setMuleContext(MuleContext muleContext)
    {
        super.setMuleContext(muleContext);
        for (Transformer transformer : transformers)
        {
            transformer.setMuleContext(muleContext);
        }
    }

    @Override
    protected String generateTransformerName()
    {
        String name = transformers.get(0).getClass().getSimpleName();
        int i = name.indexOf("To");
        DataType dt = transformers.get(transformers.size() -1).getReturnDataType();
        if (i > 0 && dt != null)
        {
            String target = dt.getType().getSimpleName();
            if (target.equals("byte[]"))
            {
                target = "byteArray";
            }
            name = name.substring(0, i + 2) + StringUtils.capitalize(target);
        }
        return name;
    }

    public List<Transformer> getTransformers()
    {
        return Collections.unmodifiableList(transformers);
    }
}
