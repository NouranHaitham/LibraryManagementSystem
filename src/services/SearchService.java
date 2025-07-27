package services;

import java.util.ArrayList;
import java.util.List;

public class SearchService<T extends Searchable> {

    private final List<T> items;

    public SearchService(List<T> items)
    {
        this.items = items;
    }

    public T searchByID(String id){
        return items.stream().filter(item -> item.getID().equals(id)).findFirst().orElse(null);
    }

    public T searchByName(String name){
        return items.stream().filter(item -> item.getName().equals(name)).findFirst().orElse(null);
    }
}
