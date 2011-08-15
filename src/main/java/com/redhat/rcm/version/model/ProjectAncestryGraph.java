/*
 *  Copyright (C) 2011 John Casey.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.redhat.rcm.version.model;

import java.util.Collection;

import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Parent;
import org.commonjava.emb.graph.DirectionalEdge;
import org.commonjava.emb.graph.SimpleDirectedGraph;

public class ProjectAncestryGraph
    extends SimpleDirectedGraph<FullProjectKey>
{

    public ProjectAncestryGraph( final FullProjectKey toolchainKey )
    {
        if ( toolchainKey != null )
        {
            getNakedGraph().addVertex( toolchainKey );
        }
    }

    public void connect( final Project project )
    {
        final FullProjectKey projectKey = project.getKey();
        if ( !getNakedGraph().containsVertex( projectKey ) )
        {
            getNakedGraph().addVertex( projectKey );
        }

        final Parent parent = project.getModel().getParent();
        if ( parent != null )
        {
            final FullProjectKey parentKey = new FullProjectKey( parent );
            if ( getNakedGraph().containsVertex( parentKey ) )
            {
                connect( projectKey, parentKey );
            }
        }
    }

    public boolean hasParentInGraph( final Project current )
    {
        final FullProjectKey currentKey = current.getKey();
        final Collection<DirectionalEdge<FullProjectKey>> outEdges = getNakedGraph().getOutEdges( currentKey );
        if ( outEdges != null && !outEdges.isEmpty() )
        {
            return true;
        }

        return false;
    }

    public boolean hasAncestor( final FullProjectKey ancestorKey, final Project current )
    {
        FullProjectKey currentKey = current.getKey();
        while ( currentKey != null )
        {
            if ( currentKey.equals( ancestorKey ) )
            {
                return true;
            }

            Collection<DirectionalEdge<FullProjectKey>> outEdges = getNakedGraph().getOutEdges( currentKey );
            if ( outEdges != null && !outEdges.isEmpty() )
            {
                currentKey = outEdges.iterator().next().getTo();
            }
            else
            {
                break;
            }
        }

        return false;
    }

    public boolean contains( final FullProjectKey key )
    {
        return getNakedGraph().containsVertex( key );
    }

    // private static final class ParentFirstComparator
    // implements Comparator<Project>
    // {
    // @Override
    // public int compare( final Project one, final Project two )
    // {
    // int result = 0;
    //
    // final Parent oneParent = one.getParent();
    // final Parent twoParent = two.getParent();
    //
    // final VersionlessProjectKey oneId = new VersionlessProjectKey( one );
    // final VersionlessProjectKey twoId = new VersionlessProjectKey( two );
    //
    // if ( oneParent != null && new VersionlessProjectKey( oneParent ).equals( twoId ) )
    // {
    // result = 1;
    // }
    // else if ( twoParent != null && new VersionlessProjectKey( twoParent ).equals( oneId ) )
    // {
    // result = -1;
    // }
    //
    // return result;
    // }
    //
    // }
}
