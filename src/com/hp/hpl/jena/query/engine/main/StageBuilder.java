/*
 * (c) Copyright 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.query.engine.main;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.core.ARQConstants;
import com.hp.hpl.jena.query.core.BasicPattern;
import com.hp.hpl.jena.query.engine.ExecutionContext;
import com.hp.hpl.jena.query.engine.QueryIterator;
import com.hp.hpl.jena.query.engine.iterator.QueryIterDistinguishedVars;
import com.hp.hpl.jena.query.util.Context;

public class StageBuilder
{
    public static boolean rawMode = true ;
    
    public static QueryIterator compile(BasicPattern pattern, 
                                        QueryIterator input, 
                                        ExecutionContext execCxt)
    {
        if ( pattern.isEmpty() )
            return input ;
        
        StageGenerator gen = chooseStageGenerator(execCxt.getContext()) ;
        StageList sList = gen.compile(pattern, execCxt) ;
        QueryIterator qIter = sList.build(input, execCxt) ;
        
        // Remove nondistinguished variables here.
        // Can't do at any one stage because two stages may share a 
        // nondistinguished variable.
        if ( ! rawMode )
            qIter = new QueryIterDistinguishedVars(qIter, execCxt) ;
        return qIter ;
    }
    
    // -------- Manage StageGenerator registration
    
    public static void setGenerator(Context context, StageGenerator builder)
    {
        context.set(ARQConstants.stageGenerator, builder) ;
    }
    
    public static StageGenerator getGenerator(Context context)
    {
        if ( context == null )
            return null ;
        return (StageGenerator)context.get(ARQConstants.stageGenerator) ;
        
        // fixed and hard wired - temp
        //return new StageGenPropertyFunction(new StageGenBasicPattern()) ;
    }
    
    public static StageGenerator getGenerator()
    {
        StageGenerator gen = getGenerator(ARQ.getContext()) ;
        if ( gen == null )
        {
            gen = standardGenerator() ;
            setGenerator(ARQ.getContext(), gen) ;
        }
        return gen ;
    }
    
    public static StageGenerator standardGenerator()
    {
        return 
        new StageGenPropertyFunction(new StageGenBasicPattern()) ;
    }
    
    private static StageGenerator chooseStageGenerator(Context context)
    {
        StageGenerator gen = getGenerator(context) ;
        if ( gen == null )
            gen = getGenerator() ;
        return gen ; 
    }
}

/*
 * (c) Copyright 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */