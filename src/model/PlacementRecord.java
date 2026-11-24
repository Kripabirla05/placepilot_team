package model;

/** Maps to a placement row, with an optional job or internship reference. */
public class PlacementRecord {
    private int id;
    private int studentId;
    private Integer jobId;
    private Integer internshipId;
    private double packageLpa;
    private String ppoStatus;

    public PlacementRecord() { }

    public PlacementRecord(int id, int studentId, Integer jobId, Integer internshipId,
                           double packageLpa, String ppoStatus) {
        setId(id);
        setStudentId(studentId);
        setOpportunityIds(jobId, internshipId);
        setPackageLpa(packageLpa);
        setPpoStatus(ppoStatus);
    }

    public int getId() { return id; }
    public void setId(int id) { if (id < 0) throw new IllegalArgumentException("id cannot be negative"); this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { if (studentId <= 0) throw new IllegalArgumentException("studentId must be positive"); this.studentId = studentId; }
    public Integer getJobId() { return jobId; }
    public void setJobId(Integer jobId) { setOpportunityIds(jobId, jobId == null ? internshipId : null); }
    public Integer getInternshipId() { return internshipId; }
    public void setInternshipId(Integer internshipId) { setOpportunityIds(internshipId == null ? jobId : null, internshipId); }
    public void setOpportunityIds(Integer jobId, Integer internshipId) {
        if (jobId != null && internshipId != null) throw new IllegalArgumentException("At most one opportunity id may be set");
        if (jobId != null && jobId <= 0 || internshipId != null && internshipId <= 0) throw new IllegalArgumentException("Opportunity id must be positive");
        this.jobId = jobId;
        this.internshipId = internshipId;
    }
    public double getPackageLpa() { return packageLpa; }
    public void setPackageLpa(double packageLpa) {
        if (!Double.isFinite(packageLpa) || packageLpa < 0) throw new IllegalArgumentException("packageLpa must be non-negative");
        this.packageLpa = packageLpa;
    }
    public String getPpoStatus() { return ppoStatus; }
    public void setPpoStatus(String ppoStatus) {
        if (ppoStatus == null || ppoStatus.trim().isEmpty()) throw new IllegalArgumentException("ppoStatus is required");
        this.ppoStatus = ppoStatus.trim();
    }
    public boolean hasOpportunity() { return jobId != null || internshipId != null; }
}
