package model;

/** Maps to the internship table. */
public class Internship extends Opportunity {
    private double minCgpa;
    private String requiredSkills;
    private boolean paid;
    private double stipend;
    private int durationMonths;

    public Internship() { }

    public Internship(int id, int companyId, String role, double minCgpa,
                      String requiredSkills, boolean paid, double stipend, int durationMonths) {
        super(id, companyId, role);
        setMinCgpa(minCgpa);
        setRequiredSkills(requiredSkills);
        setPaid(paid);
        setStipend(stipend);
        setDurationMonths(durationMonths);
    }

    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) {
        if (!Double.isFinite(minCgpa) || minCgpa < 0 || minCgpa > 10) throw new IllegalArgumentException("minCgpa must be between 0 and 10");
        this.minCgpa = minCgpa;
    }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills == null ? "" : requiredSkills.trim(); }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public double getStipend() { return stipend; }
    public void setStipend(double stipend) {
        if (!Double.isFinite(stipend) || stipend < 0) throw new IllegalArgumentException("stipend must be non-negative");
        this.stipend = stipend;
    }
    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) {
        if (durationMonths <= 0) throw new IllegalArgumentException("durationMonths must be positive");
        this.durationMonths = durationMonths;
    }
}
