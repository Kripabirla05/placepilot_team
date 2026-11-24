package model;

/** Maps to the job table. Monetary packages are stored in LPA. */
public class Job extends Opportunity {
    private String location;
    private double lpa;
    private double minCgpa;
    private String requiredSkills;
    private String jobType;
    private int probationMonths;

    public Job() { }

    public Job(int id, int companyId, String role, String location, double lpa,
               double minCgpa, String requiredSkills, String jobType, int probationMonths) {
        super(id, companyId, role);
        setLocation(location);
        setLpa(lpa);
        setMinCgpa(minCgpa);
        setRequiredSkills(requiredSkills);
        setJobType(jobType);
        setProbationMonths(probationMonths);
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = required(location, "location"); }
    public double getLpa() { return lpa; }
    public void setLpa(double lpa) {
        if (!Double.isFinite(lpa) || lpa < 0) throw new IllegalArgumentException("lpa must be non-negative");
        this.lpa = lpa;
    }
    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) {
        if (!Double.isFinite(minCgpa) || minCgpa < 0 || minCgpa > 10) throw new IllegalArgumentException("minCgpa must be between 0 and 10");
        this.minCgpa = minCgpa;
    }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills == null ? "" : requiredSkills.trim(); }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = required(jobType, "jobType"); }
    public int getProbationMonths() { return probationMonths; }
    public void setProbationMonths(int probationMonths) {
        if (probationMonths < 0) throw new IllegalArgumentException("probationMonths cannot be negative");
        this.probationMonths = probationMonths;
    }
    private static String required(String value, String field) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " is required");
        return value.trim();
    }
}
