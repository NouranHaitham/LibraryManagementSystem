package entities;

public class Admin extends User{

    public Admin(String id, String name)
    {
        super(id,name);
    }

    @Override
    public String toString() {
        return "Admin{}";
    }
}
