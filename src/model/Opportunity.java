package model;

/** Common fields shared by job and internship opportunities. */
public abstract class Opportunity {
    private int id;
    private int companyId;
    private String role;

    protected Opportunity() { }

    protected Opportunity(int id, int companyId, String role) {
        setId(id);
        setCompanyId(companyId);
        setRole(role);
    }

    public int getId() { return id; }
    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("id cannot be negative");
        this.id = id;
    }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) {
        if (companyId < 0) throw new IllegalArgumentException("companyId cannot be negative");
        this.companyId = companyId;
    }
    public String getRole() { return role; }
    public void setRole(String role) {
        if (role == null || role.trim().isEmpty()) throw new IllegalArgumentException("role is required");
        this.role = role.trim();
    }
}
