package design.algori.locph;

import java.util.HashSet;

public class MyHashSet {

    HashSet<Integer> set = new HashSet<Integer>();
    // HashSet<Integer> hs = new HashSet<Integer>();

    public MyHashSet() {
        // this.set = new HashSet<Integer>();
    }

    public void add(int key) {
        if (set.contains(key))
            return;
        set.add(key);
    }

    public void remove(int key) {
        if (set.contains(key)) {
            set.remove(key);

        }

    }

    public boolean contains(int key) {
        return set.contains(key);
    }

    /**
     * Your MyHashSet object will be instantiated and called as such:
     * MyHashSet obj = new MyHashSet();
     * obj.add(key);
     * obj.remove(key);
     * boolean param_3 = obj.contains(key);
     */

    public static void main(String[] args) {

        int key = 1;

        MyHashSet obj = new MyHashSet();
        obj.add(key);
        obj.remove(key);
        boolean param_3 = obj.contains(key);

        return;
    }
}