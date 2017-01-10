package pathdb.kpathindex;

import com.pathdb.pathIndex.Path;
import com.pathdb.pathIndex.PathPrefix;

import java.io.IOException;

/**
 * KPathIndex interface defining the public methods of the kpathindex package
 */
public interface KPathIndex {

  /**
   * Search method to lookup paths in the KPathIndex
   *
   * @param pathPrefix The prefix of a path which we need to search
   * @return An Iterable with Paths which satisfy the pathPrefix
   */
  Iterable<Path> search(PathPrefix pathPrefix) throws IOException;

  /**
   * Insert method to insert a Path into the KPathIndex
   *
   * @param path The path we will insert into the KPathIndex
   */
  void insert(Path path);

}
