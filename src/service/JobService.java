package service;

import dao.CompanyDAO;
import dao.OpportunityDAO;
import exception.AppException;
import exception.InvalidInputException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import model.Company;
import model.Internship;
import model.Job;
import model.Opportunity;
import model.Student;
import notification.NotificationService;
import utility.Util;

public class JobService {

    private final OpportunityDAO opportunityDAO = new OpportunityDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();
    private final NotificationService notifier = NotificationService.getInstance();

    private void requireVerified(int companyId) throws SQLException {
        Company c = companyDAO.findById(companyId);
        if (c == null || !c.isVerified()) {
            throw new InvalidInputException("Your company is not verified by admin yet. You can not post.");
        }
    }

    public void postJob(int companyId, String role, String location, double lpa, double minCgpa,
                        String skills, String jobType, int probationMonths) throws SQLException {
        requireVerified(companyId);
        if (Util.isEmpty(role) || Util.isEmpty(location)) {
            throw new InvalidInputException("Role and location are required.");
        }
        if (lpa <= 0) {
            throw new InvalidInputException("LPA must be more than 0.");
        }
        if (!Util.isValidCgpa(minCgpa)) {
            throw new InvalidInputException("Minimum CGPA must be between 0 and 10.");
        }
        if (probationMonths < 0) {
            throw new InvalidInputException("Probation can not be negative.");
        }
        Job job = new Job(0, companyId, "", role.trim(), Util.cleanSkills(skills), minCgpa,
                location.trim(), lpa, jobType, probationMonths);
        opportunityDAO.insertJob(job);
        notifier.sendToAll("New job posted: " + role.trim() + " (" + lpa + " LPA)", 2);
    }

    public void updateJob(int companyId, int jobId, String role, String location, double lpa,
                          double minCgpa, String skills) throws SQLException {
        Job old = opportunityDAO.findJobById(jobId);
        if (old == null || old.getCompanyId() != companyId) {
            throw new InvalidInputException("Job not found in your company.");
        }
        if (lpa <= 0 || !Util.isValidCgpa(minCgpa)) {
            throw new InvalidInputException("LPA or CGPA value is not valid.");
        }
        old.setRole(role.trim());
        old.setLocation(location.trim());
        old.setLpa(lpa);
        old.setMinCgpa(minCgpa);
        old.setRequiredSkills(Util.cleanSkills(skills));
        opportunityDAO.updateJob(old);
    }

    public boolean deleteJob(int companyId, int jobId) throws SQLException {
        return opportunityDAO.deleteJob(jobId, companyId) > 0;
    }

    public void postInternship(int companyId, String role, double minCgpa, String skills,
                               boolean paid, double stipend, int durationMonths) throws SQLException {
        requireVerified(companyId);
        if (Util.isEmpty(role)) {
            throw new InvalidInputException("Role is required.");
        }
        if (!Util.isValidCgpa(minCgpa)) {
            throw new InvalidInputException("Minimum CGPA must be between 0 and 10.");
        }
        if (durationMonths <= 0) {
            throw new InvalidInputException("Duration must be at least 1 month.");
        }
        if (paid && stipend <= 0) {
            throw new InvalidInputException("Paid internship needs a stipend more than 0.");
        }
        Internship in = new Internship(0, companyId, "", role.trim(), Util.cleanSkills(skills),
                minCgpa, paid, paid ? stipend : 0, durationMonths);
        opportunityDAO.insertInternship(in);
        notifier.sendToAll("New internship posted: " + role.trim() + (paid ? " (paid)" : " (unpaid)"), 3);
    }

    public boolean deleteInternship(int companyId, int internshipId) throws SQLException {
        return opportunityDAO.deleteInternship(internshipId, companyId) > 0;
    }

    public List<Job> getJobsForStudents() throws SQLException {
        return opportunityDAO.findAllVerifiedJobs();
    }

    public List<Job> getJobsOfCompany(int companyId) throws SQLException {
        return opportunityDAO.findJobsByCompany(companyId);
    }

    public Job getJob(int jobId) throws SQLException {
        return opportunityDAO.findJobById(jobId);
    }

    public List<Internship> getInternshipsForStudents() throws SQLException {
        return opportunityDAO.findAllVerifiedInternships();
    }

    public List<Internship> getInternshipsOfCompany(int companyId) throws SQLException {
        return opportunityDAO.findInternshipsByCompany(companyId);
    }

    public Internship getInternship(int id) throws SQLException {
        return opportunityDAO.findInternshipById(id);
    }

    public String securityInfo(Job job) {
        StringBuilder sb = new StringBuilder();
        sb.append("Job            : ").append(job.getRole()).append(" at ").append(job.getCompanyName()).append("\n");
        sb.append("Job type       : ").append(job.getJobType()).append("\n");
        sb.append("Probation      : ").append(job.getProbationMonths() == 0 ? "No probation"
                : job.getProbationMonths() + " months").append("\n");
        sb.append("Company status : ").append(job.isCompanyVerified() ? "Verified by admin" : "NOT verified").append("\n");
        if (job.getJobType().equalsIgnoreCase("Permanent")) {
            sb.append("Security level : HIGH - permanent job");
        } else {
            sb.append("Security level : MEDIUM - contract job, check renewal terms");
        }
        return sb.toString();
    }

    public List<String> findProblems(Student s, Opportunity o) {
        List<String> problems = new ArrayList<>();

        if (s.getCgpa() < o.getMinCgpa()) {
            problems.add("CGPA " + s.getCgpa() + " is below required " + o.getMinCgpa());
        }
        if (s.getBacklogs() > o.getMaxBacklogs()) {
            problems.add("Backlogs " + s.getBacklogs() + " are more than allowed " + o.getMaxBacklogs());
        }

        Set<String> required = Util.toSkillSet(o.getRequiredSkills());
        if (!required.isEmpty()) {
            Set<String> mine = Util.toSkillSet(s.getSkills());
            int matched = 0;
            for (String skill : required) {
                if (mine.contains(skill)) {
                    matched++;
                }
            }
            double percent = matched * 100.0 / required.size();
            if (percent < 50) {
                problems.add("Skill match is only " + Math.round(percent) + "% (need 50%)");
            }
        }
        return problems;
    }

    public boolean isEligible(Student s, Opportunity o) {
        return findProblems(s, o).isEmpty();
    }

    public void checkOrThrow(Student s, Opportunity o) throws AppException {
        List<String> problems = findProblems(s, o);
        if (!problems.isEmpty()) {
            throw new AppException("Not eligible: " + String.join("; ", problems));
        }
    }

    public void printReport(Student s, List<? extends Opportunity> list) {
        for (Opportunity o : list) {
            List<String> problems = findProblems(s, o);
            String result = problems.isEmpty() ? "ELIGIBLE" : "NOT ELIGIBLE";
            System.out.printf("[%s %d] %s - %s : %s%n", o.getType(), o.getId(), o.getCompanyName(), o.getRole(), result);
            for (String p : problems) {
                System.out.println("      - " + p);
            }
        }
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> condition) {
        return list.stream().filter(condition).collect(Collectors.toList());
    }

    public List<Job> byMinLpa(List<Job> jobs, double minLpa) {
        return filter(jobs, j -> j.getLpa() >= minLpa);
    }

    public List<Job> byCompany(List<Job> jobs, String company) {
        String text = company.toLowerCase();
        return filter(jobs, j -> j.getCompanyName().toLowerCase().contains(text));
    }

    public List<Job> byLocation(List<Job> jobs, String location) {
        String text = location.toLowerCase();
        return filter(jobs, j -> j.getLocation().toLowerCase().contains(text));
    }

    public List<Job> byRole(List<Job> jobs, String role) {
        String text = role.toLowerCase();
        return filter(jobs, j -> j.getRole().toLowerCase().contains(text));
    }

    public List<Job> sortByLpaDesc(List<Job> jobs) {
        return jobs.stream()
                .sorted(Comparator.comparingDouble(Job::getLpa).reversed())
                .collect(Collectors.toList());
    }

    public List<Job> sortByMinCgpa(List<Job> jobs) {
        return jobs.stream()
                .sorted(Comparator.comparingDouble(Job::getMinCgpa))
                .collect(Collectors.toList());
    }

    public List<Job> topByLpa(List<Job> jobs, int n) {
        return jobs.stream().sorted().limit(n).collect(Collectors.toList());
    }
}
