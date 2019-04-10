public class Test {
  public static void main(String[] args) {
    cacheEntry a = new cacheEntry();
    cacheEntry b = new cacheEntry(0, 1,"01010",4,0);

    System.out.println(a.toStringV());
    System.out.println(b.toStringV());
    System.out.println(a.toString());
    System.out.println(b.toString());
  }
}
