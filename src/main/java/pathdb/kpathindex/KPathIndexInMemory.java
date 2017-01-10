package pathdb.kpathindex;

import com.pathdb.pathIndex.Path;
import com.pathdb.pathIndex.PathIndex;
import com.pathdb.pathIndex.PathPrefix;
import com.pathdb.pathIndex.inMemoryTree.InMemoryIndexFactory;

import java.io.IOException;

/**
 * InMemory implementation of the KPathIndex
 */
public class KPathIndexInMemory implements KPathIndex {

  private PathIndex pathIndex;

  /**
   * Constructor which populates our pathIndex variable with the InMemoryIndex obtained from the
   * InMemoryIndexFactory from the com.pathdb package
   */
  public KPathIndexInMemory() {
    this.pathIndex = new InMemoryIndexFactory().getInMemoryIndex();
  }

  /**
   * Search method to lookup paths in the KPathIndex
   *
   * @param pathPrefix The prefix of a path which we need to search
   * @return An Iterable with Paths which satisfy the pathPrefix
   */
  @Override
  public Iterable<Path> search(PathPrefix pathPrefix) throws IOException {
    return pathIndex.getPaths(pathPrefix);
  }

  /**
   * Insert method to insert a Path into the KPathIndex
   *
   * @param path The path we will insert into the KPathIndex
   */
  @Override
  public void insert(Path path) {
    pathIndex.insert(path);
  }
}
