package entities;

public class RegularUser extends User{
    public RegularUser(String id, String name)
    {
        super(id,name);
    }
    @Override
    public String toString() {
        return "RegularUser{}";
    }
}
