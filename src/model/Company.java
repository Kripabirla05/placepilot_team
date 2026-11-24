package model;

public class Company implements Displayable {

    private int id;
    private String name;
    private String location;
    private boolean verified;

    public Company(int id, String name, String location, boolean verified) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.verified = verified;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public boolean isVerified() { return verified; }

    @Override
    public void display() {
        System.out.printf("%-4d %-15s %-12s %s%n", id, name, location, verified ? "Verified" : "NOT verified");
    }
}
