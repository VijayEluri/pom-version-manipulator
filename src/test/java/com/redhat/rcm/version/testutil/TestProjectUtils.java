package com.redhat.rcm.version.testutil;

import static junit.framework.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.mae.project.ProjectToolsException;
import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import com.redhat.rcm.version.VManException;

public final class TestProjectUtils
{

    private TestProjectUtils()
    {
    }

    public static File getResourceFile( final String path )
    {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource( path );
        if ( resource == null )
        {
            fail( "Resource not found: " + path );
        }

        return new File( resource.getPath() );
    }

    public static Model loadModel( final String path )
        throws IOException
    {
        final File pom = getResourceFile( path );
        return loadModel( pom );
    }

    public static Model loadModel( final File pom )
        throws IOException
    {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( ModelReader.IS_STRICT, Boolean.FALSE.toString() );

        return new DefaultModelReader().read( pom, options );
    }

    public static FullProjectKey loadProjectKey( final String path )
        throws ProjectToolsException, IOException
    {
        Model model = loadModel( path );

        return new FullProjectKey( model );
    }

    public static FullProjectKey loadProjectKey( final File pom )
        throws ProjectToolsException, IOException
    {
        Model model = loadModel( pom );

        return new FullProjectKey( model );
    }

    public static Set<Model> loadModels( final Set<File> poms )
        throws VManException, IOException
    {
        Set<Model> models = new LinkedHashSet<Model>( poms.size() );
        for ( File pom : poms )
        {
            models.add( loadModel( pom ) );
        }

        return models;
    }

    public static void dumpModel( final Model model )
        throws IOException
    {
        StringWriter writer = new StringWriter();
        new MavenXpp3Writer().write( writer, model );

        System.out.println( "\n\n" + writer.toString() + "\n\n" );
    }
}