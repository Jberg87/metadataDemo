package nl.ns.bi_automation.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class);


    /**
     * This method will check whether the artifact is properly created in the target output directory or not. If artifact got properly created than further call a method to check its contents and assert true or false
     *
     * @param referenceDir Path to the reference file
     * @param generationDir Path to the generated file
     * @param filename  Static/input artifact file name
     * @throws IOException File handling Exception
     */
    public void assertFileExistAndEqualsReference( String referenceDir, String generationDir, String filename) throws IOException {
        String refPath = Paths.get( referenceDir + filename ).toString();
        String genPath = Paths.get( generationDir + filename ).toString();

        if ( !new File( refPath ).exists() ) {
            logger.debug( "Files in reference directory:" );
            String[] files = new File( referenceDir ).list();
            Arrays.stream( Objects.requireNonNull( files ) ).forEach( logger::debug );
            fail( "Reference file not present: " + refPath );
        }

        if ( !new File( genPath ).exists() ) {
            logger.debug( "Files in generation directory:" );
            String[] files = new File( generationDir ).list();
            Arrays.stream( Objects.requireNonNull( files ) ).forEach( logger::debug );
            fail( "Generated file is not present: " + genPath );
        }

        assertEqualsReferenceWithArtifactFileContent( refPath, genPath );
    }


    /**
     * This method checks if two files are identical or not. One file is the references file, the other the file in the resources directory (to be deployed to Artifactory)
     *
     * @param referenceFilePath Path to the static reference artifact
     * @param artifactFileName  Reference/resources artifact file name
     * @throws IOException File handling Exception
     */
    public void assertFilePresentAndInAssembly( String referenceFilePath, String artifactFileName ) throws IOException {

        String resourcesArtifactsDirectory = "src/main/resources/artifactory/";
        String assemblyBuildFilePath = "src/main/assembly/build.xml";

        // Check if reference file is in the expected location
        assertTrue( new File( referenceFilePath + artifactFileName ).exists() );

        // Check if resources file is in the expected location
        assertTrue( new File( resourcesArtifactsDirectory + artifactFileName ).exists() );

        // Compare the reference and resources artifact
        assertEqualsReferenceWithArtifactFileContent( referenceFilePath, resourcesArtifactsDirectory + "/" + artifactFileName );

        // Check if the filename is in the assembly build file and is not commented out (starts with '<!--' and/or ends with '-->')
        String assemblyBuildContent = new String( Files.readAllBytes( Paths.get( assemblyBuildFilePath ) ) );
        assertTrue( assemblyBuildContent.contains( "<include>" + artifactFileName + "</include>" )  );
        assertFalse( assemblyBuildContent.contains( "<include>" + artifactFileName + "</include>-->" )  );
    }


    /**
     * This method will check check the content of input and generated artifacts and asserts true if it matches else false
     *
     * @param refPath Path to the reference file
     * @param genPath Path to the generated artifact
     * @throws IOException File handling Exception
     */
    private void assertEqualsReferenceWithArtifactFileContent( String refPath, String genPath ) throws IOException {

        // Read file from disk to memory
        String refContent = new String( Files.readAllBytes( Paths.get( refPath ) ) );
        String genContent = new String( Files.readAllBytes( Paths.get( genPath ) ) );

        genContent = removeDynamicContentAndChangeFileFormat( genContent );
        refContent = removeDynamicContentAndChangeFileFormat( refContent );

        assertEquals( refContent, genContent );
    }

    private String removeDynamicContentAndChangeFileFormat( String content ) {
        return content.replaceAll( "\r\n", "\n" ).replaceAll( "\n", "\r\n" ) // Convert the line endings in the content from UNIX to DOS format (DOS uses \r\n for line termination, while UNIX uses a single \n)
                .replaceAll( "(?!9999-12-31)\\d{4}-\\d{2}-\\d{2}", "" ) // Date replacement, Leave 9999-12-31 for valid from selection intact
                // Version number replacement
                .replaceAll( "-SNAPSHOT", "" ) // remove -SNAPSHOT from version number
                .replaceAll( "(?!00:00:00)\\d{1,2}[.:]\\d{1,2}[.:]\\d{1,2}", "" ); // Leave 00:00:00 for valid from selection intact
    }
}
