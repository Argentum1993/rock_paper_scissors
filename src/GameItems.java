public class GameItems {
  private Node[] nodes;
  private int median;

  public GameItems(String[] items){
    this.nodes = new Node[items.length];
    this.median = items.length / 2;
    for (int i = 0; i < items.length; i++) {
      nodes[i] = new Node(items[i]);
    }
    Node prev = this.nodes[this.nodes.length - 1];
    for (int i = 0; i < nodes.length; i++) {
     nodes[i].prev = prev;
     nodes[i].next = nodes[(i + 1) < nodes.length ? i + 1 : 0];
     prev = nodes[i];
    }
  }

  public String getRandomItem(){
    return nodes[(int)(Math.random() * nodes.length)].name;
  }

  public String get(int index){
    if (index < 0 || index >= nodes.length)
      return null;
    return nodes[index].name;
  }

  public int size(){
    return nodes.length;
  }

  public String fight(String first, String second){
    Node iterator;
    if (first.equals(second))
      return null;
    for (Node node : nodes){
      if (node.name.equals(first)){
        iterator = node.next;
        for (int i = 0; i < median; i++) {
          if (iterator.name.equals(second))
            return second;
          iterator = iterator.next;
        }
        return first;
      }
    }
    return null;
  }

  private class Node{
    String name;
    Node next;
    Node prev;

    public Node(String name){
      this.name = name;
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    for (Node node : nodes){
      builder.append(node.name);
      builder.append(" next:" + node.next.name);
      builder.append(" prev:" + node.prev.name);
      builder.append('\n');
    }
    return builder.toString();
  }
}
