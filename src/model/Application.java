package model;

import java.time.LocalDate;
import java.util.Locale;

/** Maps to an application row; exactly one of jobId and internshipId should be set. */
public class Application {
    private int id;
    private int studentId;
    private Integer jobId;
    private Integer internshipId;
    private String status;
    private LocalDate appliedDate;

    public Application() { }

    public Application(int id, int studentId, Integer jobId, Integer internshipId,
                       String status, LocalDate appliedDate) {
        setId(id);
        setStudentId(studentId);
        setOpportunityIds(jobId, internshipId);
        setStatus(status);
        setAppliedDate(appliedDate);
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
        if ((jobId == null) == (internshipId == null)) throw new IllegalArgumentException("Exactly one of jobId or internshipId is required");
        if (jobId != null && jobId <= 0 || internshipId != null && internshipId <= 0) throw new IllegalArgumentException("Opportunity id must be positive");
        this.jobId = jobId;
        this.internshipId = internshipId;
    }
    public boolean isForJob() { return jobId != null; }
    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) throw new IllegalArgumentException("status is required");
        this.status = status.trim();
    }
    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { if (appliedDate == null) throw new IllegalArgumentException("appliedDate is required"); this.appliedDate = appliedDate; }
    public boolean isSelected() { return "selected".equals(status.toLowerCase(Locale.ROOT)); }
}
