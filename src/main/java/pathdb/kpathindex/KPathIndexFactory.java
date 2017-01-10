package pathdb.kpathindex;

/**
 * This KPathIndexFactory will be used to abstract away if we use the InMemory or DiskBased
 * KPathIndex
 */
public class KPathIndexFactory {

  private static KPathIndex kPathIndex;

  /**
   * For now we are using the InMemory KPathIndex, which we have defined here in this factory
   *
   * @return The kPathIndex
   */
  public static KPathIndex getKPathIndex() {

    if (kPathIndex == null) {
      kPathIndex = new KPathIndexInMemory();
    }
    return kPathIndex;

  }
}
