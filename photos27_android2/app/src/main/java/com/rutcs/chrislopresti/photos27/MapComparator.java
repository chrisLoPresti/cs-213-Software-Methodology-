package com.rutcs.chrislopresti.photos27;

import java.util.Comparator;
import java.util.HashMap;

public class MapComparator implements Comparator<HashMap<String,String>> {
    private final String key;
    private final String order;

    public MapComparator(String key,String order) {
        this.key = key;
        this.order = order;
    }

    @Override
    public int compare(HashMap<String, String> first, HashMap<String, String> second) {
        try {
            String fv = first.get(key);
            String sv = second.get(key);
            if(this.order.toLowerCase().contentEquals("asc")) {
                return fv.compareTo(sv);
            }
            return sv.compareTo(fv);
        } catch (NullPointerException ex) {
            System.out.println("Bad: " + ex.getMessage());
            return -3;
        }
    }
}
