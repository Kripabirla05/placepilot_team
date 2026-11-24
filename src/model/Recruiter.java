package model;

public class Recruiter extends User {

    private int companyId;

    public Recruiter(int id, String name, String email, String password, int companyId) {
        super(id, name, email, password);
        this.companyId = companyId;
    }

    public int getCompanyId() { return companyId; }

    @Override
    public String getRole() { return "Recruiter"; }

    @Override
    public void display() {
        System.out.println("Recruiter: " + name + " (" + email + "), Company ID: " + companyId);
    }
}
