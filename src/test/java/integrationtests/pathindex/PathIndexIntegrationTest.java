package integrationtests.pathindex;

import com.pathdb.pathIndex.Node;
import com.pathdb.pathIndex.Path;
import com.pathdb.pathIndex.PathIndex;
import com.pathdb.pathIndex.PathPrefix;
import com.pathdb.pathIndex.inMemoryTree.InMemoryIndexFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PathIndexIntegrationTest
{
    @Test
    public void pathIndexIntegrationTest() throws Exception
    {
        // given
        PathIndex index = new InMemoryIndexFactory().getInMemoryIndex();

        // when
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add( new Node( 1 ) );
        nodes.add( new Node( 2 ) );
        nodes.add( new Node( 3 ) );
        Path path = new Path( 42, nodes );
        index.insert( path );

        // then
        Iterable<Path> paths = index.getPaths( new PathPrefix( 42, 3 ) );
        Iterator<Path> iterator = paths.iterator();
        Path next = iterator.next();
        assertEquals( "Should have found the same path in the index.", path, next );
        assertFalse( iterator.hasNext() );
    }
}
