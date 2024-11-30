package it.unibo.field4s.collections

import scala.annotation.targetName

import it.unibo.field4s.implementations.collections.MapValueTree

/**
 * A tree-like structure that maps sequences of nodes to values.
 * @tparam N
 *   the type of the nodes
 * @tparam V
 *   the type of the values
 */
trait ValueTree[N, +V] extends Iterable[(Seq[N], V)] with PartialFunction[Seq[N], V]:
  /**
   * Checks if the tree contains the given sequence of nodes.
   * @param seq
   *   the sequence of nodes
   * @return
   *   true if the tree contains the sequence, false otherwise
   */
  def contains(seq: Seq[N]): Boolean

  /**
   * Checks if the tree contains the given sequence of nodes as a prefix of some sequence.
   * @param seq
   *   the sequence of nodes
   * @return
   *   true if the tree contains the sequence as a prefix, false otherwise
   */
  def containsPrefix(seq: Iterable[N]): Boolean

  /**
   * Gets the value associated with the given sequence of nodes.
   * @param seq
   *   the sequence of nodes
   * @return
   *   the value associated with the sequence, if any
   */
  def get(seq: Seq[N]): Option[V]

  /**
   * Maps the values of the tree.
   * @param f
   *   the mapping function
   * @tparam V1
   *   the type of the new values
   * @return
   *   a new tree with the mapped values
   */
  def mapValues[V1](f: (Seq[N], V) => V1): ValueTree[N, V1]

  /**
   * Maps the nodes of the tree.
   * @param f
   *   the mapping function
   * @tparam N1
   *   the type of the new nodes
   * @return
   *   a new tree with the mapped nodes
   */
  def mapNodes[N1](f: N => N1): ValueTree[N1, V]

  /**
   * Changes the path of every value in the tree by mapping path-value pairs to new path-value pairs.
   * @param f
   *   the mapping function
   * @tparam N1
   *   the type of the new nodes
   * @tparam V1
   *   the type of the new values
   * @return
   *   a new tree with the mapped paths
   */
  def map[N1, V1](f: (Seq[N], V) => (Seq[N1], V1)): ValueTree[N1, V1]

  /**
   * Filters the tree by keeping only the values that satisfy the given predicate.
   * @param f
   *   the filtering function
   * @return
   *   a new tree with the filtered values
   */
  def filter(f: (Seq[N], V) => Boolean): ValueTree[N, V]

  /**
   * Filters the tree by keeping only the values that do not satisfy the given predicate.
   * @param f
   *   the filtering function
   * @return
   *   a new tree with the filtered values
   */
  def filterNot(f: (Seq[N], V) => Boolean): ValueTree[N, V] =
    filter((k, v) => !f(k, v))

  /**
   * Flattens the tree by mapping each path-value pair to a sequence of new path-value pairs.
   * @param f
   *   the mapping function
   * @tparam N1
   *   the type of the new nodes
   * @tparam V1
   *   the type of the new values
   * @return
   *   a new tree with the flattened paths
   */
  def flatMap[N1, V1](
      f: (Seq[N], V) => IterableOnce[(Seq[N1], V1)],
  ): ValueTree[N1, V1]

  /**
   * Removes the given sequence of nodes from the tree.
   * @param seq
   *   the sequence of nodes
   * @return
   *   a new tree without the given sequence
   */
  def remove(seq: Seq[N]): ValueTree[N, V]

  /**
   * Removes all the sequences of nodes that start with the given sequence from the tree.
   * @param seq
   *   the sequence of nodes
   * @return
   *   a new tree without the given sequence as a prefix
   */
  def removePrefix(seq: Iterable[N]): ValueTree[N, V]

  /**
   * Updates the value associated with the given sequence of nodes.
   * @param seq
   *   the sequence of nodes
   * @param value
   *   the new value
   * @tparam V1
   *   the type of the new value
   * @return
   *   a new tree with the updated value
   */
  def update[V1 >: V](seq: Seq[N], value: V1): ValueTree[N, V1]

  /**
   * Concatenates the tree with another tree.
   * @param other
   *   the other tree
   * @tparam V1
   *   the type of the values of the other tree
   * @return
   *   a new tree with the concatenated values
   */
  def concat[V1 >: V](other: ValueTree[N, V1]): ValueTree[N, V1]

  /**
   * Partitions the tree into two trees according to the given predicate.
   * @param f
   *   the partitioning function
   * @return
   *   a pair of trees
   */
  def partition(f: (Seq[N], V) => Boolean): (ValueTree[N, V], ValueTree[N, V])

  /**
   * Prepends a sequence of nodes to every path in the tree.
   * @param prefix
   *   the sequence of nodes
   * @tparam N1
   *   the type of the new nodes
   * @return
   *   a new tree with the prepended paths
   */
  def prepend[N1 >: N](prefix: Seq[N1]): ValueTree[N1, V] =
    map((k, v) => (prefix ++ k, v))

  /**
   * Appends a sequence of nodes to every path in the tree.
   * @param suffix
   *   the sequence of nodes
   * @tparam N1
   *   the type of the new nodes
   * @return
   *   a new tree with the appended paths
   */
  def append[N1 >: N](suffix: Seq[N1]): ValueTree[N1, V] =
    map((k, v) => (k ++ suffix, v))

  /**
   * Reverses the nodes of every path in the tree.
   * @return
   *   a new tree with the reversed paths
   */
  def reversedNodes: ValueTree[N, V] = map((k, v) => (k.reverse, v))

  @targetName("concat")
  inline def ++[V1 >: V](other: ValueTree[N, V1]): ValueTree[N, V1] = concat(
    other,
  )

  @targetName("update")
  inline def +[V1 >: V](kv: (Seq[N], V1)): ValueTree[N, V1] =
    update(kv._1, kv._2)

  override def className: String = "ValueTree"

  override def apply(v1: Seq[N]): V = get(v1).get

  override def isDefinedAt(v1: Seq[N]): Boolean = contains(v1)

  override def toString(): String = super[Iterable].toString()
end ValueTree

object ValueTree extends ValueTree.Factory[ValueTree]:

  /**
   * A factory for value trees.
   * @tparam VT
   *   the type of the value tree
   */
  trait Factory[VT[N, V] <: ValueTree[N, V]]:

    /**
     * Creates an empty value tree.
     * @tparam N
     *   the type of the nodes
     * @tparam V
     *   the type of the values
     * @return
     *   an empty value tree
     */
    def empty[N, V]: VT[N, V]

    /**
     * Creates a value tree from a sequence of path-value pairs.
     * @param elems
     *   the sequence of path-value pairs
     * @tparam N
     *   the type of the nodes
     * @tparam V
     *   the type of the values
     * @return
     *   a value tree with the given path-value pairs
     */
    def apply[N, V](elems: (Seq[N], V)*): VT[N, V]

    /**
     * Creates a value tree from a sequence of value trees.
     * @param elems
     *   the sequence of value trees
     * @tparam N
     *   the type of the nodes
     * @tparam V
     *   the type of the values
     * @return
     *   a value tree with the given value trees
     */
    @targetName("merge")
    def apply[N, V](elems: ValueTree[N, V]*): VT[N, V]
  end Factory

  override def empty[N, V]: ValueTree[N, V] = MapValueTree.empty

  override def apply[N, V](elems: (Seq[N], V)*): ValueTree[N, V] = MapValueTree(
    elems*,
  )

  @targetName("merge")
  override def apply[N, V](elems: ValueTree[N, V]*): ValueTree[N, V] =
    MapValueTree(elems*)
end ValueTree
