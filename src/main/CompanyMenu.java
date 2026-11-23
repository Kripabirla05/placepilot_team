package main;

import exception.AppException;
import exception.InvalidInputException;
import java.sql.SQLException;
import java.util.List;
import model.Application;
import model.Company;
import model.Internship;
import model.Job;
import model.PlacementRecord;
import model.Recruiter;
import service.ApplicationService;
import service.UserService;
import service.JobService;
import utility.Util;

public class CompanyMenu {

    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    private Recruiter recruiter;

    public void start() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== COMPANY / RECRUITER =====");
            System.out.println("1. Register company");
            System.out.println("2. Recruiter login");
            System.out.println("0. Back");
            int choice = Util.readIntInRange("Choose: ", 0, 2);
            try {
                if (choice == 1) {
                    registerCompany();
                } else if (choice == 2) {
                    String email = Util.readString("Email: ");
                    String password = Util.readString("Password: ");
                    recruiter = userService.loginRecruiter(email, password);
                    System.out.println("Welcome, " + recruiter.getName() + "!");
                    dashboard();
                } else {
                    back = true;
                }
            } catch (AppException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (InvalidInputException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private void registerCompany() throws AppException, SQLException {
        System.out.println("\n--- Company Registration ---");
        String company = Util.readString("Company name: ");
        String location = Util.readString("Company location: ");
        String name = Util.readString("Recruiter name: ");
        String email = Util.readString("Recruiter email: ");
        String password = Util.readString("Password (min 6 chars with a digit): ");
        userService.registerCompany(company, location, name, email, password);
        System.out.println("Registered! Your company must be VERIFIED by admin before you can post jobs.");
    }

    private void dashboard() throws SQLException {
        boolean logout = false;
        while (!logout) {
            try {
                Company company = userService.getCompany(recruiter.getCompanyId());
                System.out.println("\n===== RECRUITER DASHBOARD: " + company.getName()
                        + (company.isVerified() ? " (Verified)" : " (NOT verified yet)") + " =====");
                System.out.println(" 1. Post job                   7. Delete internship");
                System.out.println(" 2. Update job                 8. View applications");
                System.out.println(" 3. Delete job                 9. Shortlist candidate");
                System.out.println(" 4. View my jobs              10. Select / Reject candidate");
                System.out.println(" 5. Post internship           11. Update PPO status of interns");
                System.out.println(" 6. View my internships");
                System.out.println(" 0. Logout");
                int choice = Util.readIntInRange("Choose: ", 0, 11);

                switch (choice) {
                    case 1:  postJob(); break;
                    case 2:  updateJob(); break;
                    case 3:  deleteJob(); break;
                    case 4:  viewJobs(); break;
                    case 5:  postInternship(); break;
                    case 6:  viewInternships(); break;
                    case 7:  deleteInternship(); break;
                    case 8:  viewApplications(); break;
                    case 9:  changeStatus("Shortlisted"); break;
                    case 10: selectOrReject(); break;
                    case 11: updatePpo(); break;
                    case 0:  logout = true; break;
                }
                if (!logout) {
                    Util.pressEnter();
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private int companyId() {
        return recruiter.getCompanyId();
    }

    private void postJob() throws SQLException {
        String role = Util.readString("Role: ");
        String location = Util.readString("Location: ");
        double lpa = Util.readDouble("LPA: ");
        double minCgpa = Util.readDouble("Minimum CGPA: ");
        String skills = Util.readString("Required skills (comma separated): ");
        int type = Util.readIntInRange("Job type (1 = Permanent, 2 = Contract): ", 1, 2);
        int probation = Util.readInt("Probation period in months (0 if none): ");
        jobService.postJob(companyId(), role, location, lpa, minCgpa, skills,
                type == 1 ? "Permanent" : "Contract", probation);
        System.out.println("Job posted. Students will get a notification.");
    }

    private void viewJobs() throws SQLException {
        List<Job> jobs = jobService.getJobsOfCompany(companyId());
        if (jobs.isEmpty()) {
            System.out.println("You have not posted any job.");
        }
        for (Job j : jobs) {
            j.display();
        }
    }

    private void updateJob() throws SQLException {
        viewJobs();
        int id = Util.readInt("Job ID to update: ");
        Job old = jobService.getJob(id);
        if (old == null || old.getCompanyId() != companyId()) {
            throw new InvalidInputException("Job not found in your company.");
        }
        System.out.println("Press Enter to keep old value.");
        String role = Util.readString("Role [" + old.getRole() + "]: ");
        String location = Util.readString("Location [" + old.getLocation() + "]: ");
        String lpaText = Util.readString("LPA [" + old.getLpa() + "]: ");
        String cgpaText = Util.readString("Min CGPA [" + old.getMinCgpa() + "]: ");
        String skills = Util.readString("Skills [" + old.getRequiredSkills() + "]: ");
        try {
            jobService.updateJob(companyId(), id,
                    role.isEmpty() ? old.getRole() : role,
                    location.isEmpty() ? old.getLocation() : location,
                    lpaText.isEmpty() ? old.getLpa() : Double.parseDouble(lpaText),
                    cgpaText.isEmpty() ? old.getMinCgpa() : Double.parseDouble(cgpaText),
                    skills.isEmpty() ? old.getRequiredSkills() : skills);
            System.out.println("Job updated.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Nothing changed.");
        }
    }

    private void deleteJob() throws SQLException {
        viewJobs();
        int id = Util.readInt("Job ID to delete: ");
        if (Util.readYesNo("Are you sure?")) {
            System.out.println(jobService.deleteJob(companyId(), id) ? "Job deleted." : "Job not found.");
        }
    }

    private void postInternship() throws SQLException {
        String role = Util.readString("Internship role: ");
        double minCgpa = Util.readDouble("Minimum CGPA: ");
        String skills = Util.readString("Required skills (comma separated): ");
        boolean paid = Util.readYesNo("Is it paid?");
        double stipend = paid ? Util.readDouble("Stipend per month (Rs.): ") : 0;
        int months = Util.readInt("Duration in months: ");
        jobService.postInternship(companyId(), role, minCgpa, skills, paid, stipend, months);
        System.out.println("Internship posted.");
    }

    private void viewInternships() throws SQLException {
        List<Internship> list = jobService.getInternshipsOfCompany(companyId());
        if (list.isEmpty()) {
            System.out.println("You have not posted any internship.");
        }
        for (Internship in : list) {
            in.display();
        }
    }

    private void deleteInternship() throws SQLException {
        viewInternships();
        int id = Util.readInt("Internship ID to delete: ");
        if (Util.readYesNo("Are you sure?")) {
            System.out.println(jobService.deleteInternship(companyId(), id) ? "Deleted." : "Not found.");
        }
    }

    private boolean viewApplications() throws SQLException {
        List<Application> list = applicationService.getCompanyApplications(companyId());
        if (list.isEmpty()) {
            System.out.println("No applications yet.");
            return false;
        }
        Util.title("Applications");
        for (Application a : list) {
            System.out.printf("App #%d | %-12s | %-10s | %-20s | %s%n", a.getId(), a.getStudentName(),
                    a.isForJob() ? "Job" : "Internship", a.getRole(), a.getStatus());
        }
        return true;
    }

    private void changeStatus(String status) throws SQLException {
        if (!viewApplications()) {
            return;
        }
        int id = Util.readInt("Application ID: ");
        applicationService.changeStatus(companyId(), id, status);
        System.out.println("Status changed to " + status + ". Student notified.");
    }

    private void selectOrReject() throws SQLException {
        int choice = Util.readIntInRange("1 = Select, 2 = Reject: ", 1, 2);
        changeStatus(choice == 1 ? "Selected" : "Rejected");
    }

    private void updatePpo() throws SQLException {
        List<PlacementRecord> list = applicationService.getInternshipRecordsForCompany(companyId());
        if (list.isEmpty()) {
            System.out.println("No selected interns yet.");
            return;
        }
        for (PlacementRecord r : list) {
            r.display();
        }
        int id = Util.readInt("Record ID: ");
        int choice = Util.readIntInRange("1 = PPO Offered, 2 = No PPO: ", 1, 2);
        applicationService.updatePpo(companyId(), id, choice == 1 ? "PPO Offered" : "No PPO");
        System.out.println("PPO status updated. Student notified.");
    }
}
