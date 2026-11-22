package main;

import exception.AppException;
import exception.InvalidInputException;
import interview.InterviewService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import model.Application;
import model.Internship;
import model.Job;
import model.PlacementRecord;
import model.Student;
import model.TestResult;
import analytics.PlacementAnalytics;
import notification.NotificationService;
import service.ApplicationService;
import service.UserService;
import service.JobService;
import utility.Util;
import dao.TestResultDAO;

public class StudentMenu {

    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();
    private final PlacementAnalytics analytics = new PlacementAnalytics();
    private final NotificationService notifier = NotificationService.getInstance();
    private final TestResultDAO testResultDAO = new TestResultDAO();

    private Student student;

    public void start() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== STUDENT =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Back");
            int choice = Util.readIntInRange("Choose: ", 0, 2);
            try {
                if (choice == 1) {
                    register();
                } else if (choice == 2) {
                    login();
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

    private void register() throws AppException, SQLException {
        System.out.println("\n--- Student Registration ---");
        String name = Util.readString("Full name: ");
        String email = Util.readString("Email: ");
        String password = Util.readString("Password (min 6 chars with a digit): ");
        String branch = Util.readString("Branch (CSE/IT/ECE/ME...): ");
        double cgpa = Util.readDouble("CGPA (0-10): ");
        int backlogs = Util.readInt("Number of backlogs: ");
        String skills = Util.readString("Skills (comma separated, e.g. Java,SQL): ");

        userService.registerStudent(name, email, password, branch, cgpa, backlogs, skills);
        System.out.println("Registration successful! Now login.");
    }

    private void login() throws AppException, SQLException {
        String email = Util.readString("Email: ");
        String password = Util.readString("Password: ");
        student = userService.loginStudent(email, password);
        System.out.println("\nWelcome, " + student.getName() + "!");
        dashboard();
    }

    private void dashboard() {
        boolean logout = false;
        while (!logout) {
            try {
                student = userService.refresh(student);
                System.out.println("\n===== STUDENT DASHBOARD (" + student.getName()
                        + ") | Unread: " + notifier.unreadCount(student.getId()) + " =====");
                System.out.println(" 1. View profile             10. Skill Demand Analyzer");
                System.out.println(" 2. Update profile           11. Company comparison");
                System.out.println(" 3. Add skills               12. Placement readiness score");
                System.out.println(" 4. Search / filter jobs     13. Mock interview");
                System.out.println(" 5. Apply for a job          14. MCQ test (timer)");
                System.out.println(" 6. Internships (view/apply) 15. Notification inbox");
                System.out.println(" 7. Track applications       16. Job security information");
                System.out.println(" 8. Check eligibility        17. Internship -> PPO tracking");
                System.out.println(" 9. Skill Gap Analyzer       18. My test results");
                System.out.println(" 0. Logout");
                int choice = Util.readIntInRange("Choose: ", 0, 18);

                switch (choice) {
                    case 1:  student.display(); break;
                    case 2:  updateProfile(); break;
                    case 3:  addSkills(); break;
                    case 4:  searchJobs(); break;
                    case 5:  applyJob(); break;
                    case 6:  internships(); break;
                    case 7:  trackApplications(); break;
                    case 8:  checkEligibility(); break;
                    case 9:  skillGap(); break;
                    case 10: skillDemand(); break;
                    case 11: comparison(); break;
                    case 12: analytics.showReadiness(student); break;
                    case 13: mockInterview(); break;
                    case 14: new InterviewService().startMcq(student); break;
                    case 15: inbox(); break;
                    case 16: jobSecurity(); break;
                    case 17: ppoTracking(); break;
                    case 18: testResults(); break;
                    case 0:  logout = true; break;
                }
                if (!logout) {
                    Util.pressEnter();
                }
            } catch (AppException e) {
                System.out.println("Cannot do this: " + e.getMessage());
            } catch (InvalidInputException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }
        }
        System.out.println("Logged out.");
    }

    private void updateProfile() throws SQLException {
        System.out.println("Press Enter to keep old value.");
        String branch = Util.readString("Branch [" + student.getBranch() + "]: ");
        String cgpaText = Util.readString("CGPA [" + student.getCgpa() + "]: ");
        String backText = Util.readString("Backlogs [" + student.getBacklogs() + "]: ");

        double cgpa = cgpaText.isEmpty() ? student.getCgpa() : Double.parseDouble(cgpaText);
        int backlogs = backText.isEmpty() ? student.getBacklogs() : Integer.parseInt(backText);
        if (branch.isEmpty()) {
            branch = student.getBranch();
        }
        try {
            userService.updateAcademicDetails(student, branch, cgpa, backlogs);
            System.out.println("Profile updated.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered. Nothing was changed.");
        }
    }

    private void addSkills() throws SQLException {
        String skills = Util.readString("Skills to add (comma separated): ");
        userService.addSkills(student, skills);
        System.out.println("Skills now: " + student.getSkills());
    }

    private void printJobs(List<Job> jobs) {
        if (jobs.isEmpty()) {
            System.out.println("No jobs found.");
            return;
        }
        for (Job j : jobs) {
            j.display();
        }
    }

    private void searchJobs() throws SQLException {
        List<Job> all = jobService.getJobsForStudents();
        System.out.println("1. All jobs (highest LPA first)");
        System.out.println("2. Filter by minimum LPA");
        System.out.println("3. Filter by company name");
        System.out.println("4. Search by location");
        System.out.println("5. Search by role");
        System.out.println("6. Sort by lowest CGPA requirement");
        int choice = Util.readIntInRange("Choose: ", 1, 6);

        List<Job> result;
        switch (choice) {
            case 2:
                result = jobService.byMinLpa(all, Util.readDouble("Minimum LPA: "));
                break;
            case 3:
                result = jobService.byCompany(all, Util.readString("Company name: "));
                break;
            case 4:
                result = jobService.byLocation(all, Util.readString("Location: "));
                break;
            case 5:
                result = jobService.byRole(all, Util.readString("Role: "));
                break;
            case 6:
                result = jobService.sortByMinCgpa(all);
                break;
            default:
                result = jobService.sortByLpaDesc(all);
        }
        System.out.println("\n" + result.size() + " job(s) found:");
        printJobs(result);
    }

    private void applyJob() throws SQLException, AppException {
        List<Job> jobs = jobService.getJobsForStudents();
        printJobs(jobs);
        if (jobs.isEmpty()) {
            return;
        }
        int id = Util.readInt("Enter Job ID to apply (0 to cancel): ");
        if (id == 0) {
            return;
        }
        applicationService.applyForJob(student, id);
        System.out.println("Applied successfully!");
    }

    private void internships() throws SQLException, AppException {
        List<Internship> list = jobService.getInternshipsForStudents();
        if (list.isEmpty()) {
            System.out.println("No internships available.");
            return;
        }
        for (Internship in : list) {
            in.display();
        }
        int id = Util.readInt("Enter Internship ID to apply (0 to go back): ");
        if (id == 0) {
            return;
        }
        applicationService.applyForInternship(student, id);
        System.out.println("Applied for internship successfully!");
    }

    private void trackApplications() throws SQLException {
        List<Application> list = applicationService.getMyApplications(student.getId());
        if (list.isEmpty()) {
            System.out.println("You have not applied anywhere yet.");
            return;
        }
        Util.title("My Applications");
        for (Application a : list) {
            a.display();
        }
    }

    private void checkEligibility() throws SQLException {
        Util.title("Placement eligibility for jobs");
        jobService.printReport(student, jobService.getJobsForStudents());
        Util.title("Eligibility for internships");
        jobService.printReport(student, jobService.getInternshipsForStudents());
    }

    private void skillGap() throws SQLException {
        int type = Util.readIntInRange("1 = Job, 2 = Internship: ", 1, 2);
        if (type == 1) {
            printJobs(jobService.getJobsForStudents());
            Job job = jobService.getJob(Util.readInt("Job ID: "));
            if (job == null) {
                throw new InvalidInputException("Job not found.");
            }
            analytics.showSkillGap(student, job);
        } else {
            List<Internship> list = jobService.getInternshipsForStudents();
            for (Internship in : list) {
                in.display();
            }
            Internship in = jobService.getInternship(Util.readInt("Internship ID: "));
            if (in == null) {
                throw new InvalidInputException("Internship not found.");
            }
            analytics.showSkillGap(student, in);
        }
    }

    private void skillDemand() throws SQLException {
        List<Job> jobs = jobService.getJobsForStudents();
        List<Internship> internships = jobService.getInternshipsForStudents();
        Util.title("Skill demand in JOBS");
        analytics.showTopSkills(jobs, 5);
        Util.title("Skill demand in INTERNSHIPS");
        analytics.showTopSkills(internships, 5);
    }

    private void comparison() throws SQLException {
        List<Job> jobs = jobService.getJobsForStudents();
        printJobs(jobs);
        if (jobs.size() < 2) {
            System.out.println("Need at least 2 jobs to compare.");
            return;
        }
        Job a = jobService.getJob(Util.readInt("First Job ID: "));
        Job b = jobService.getJob(Util.readInt("Second Job ID: "));
        if (a == null || b == null) {
            throw new InvalidInputException("Job ID not found.");
        }
        analytics.compareJobs(a, b);
        System.out.println();
        analytics.showBestLpaByCompany(jobs);
    }

    private void mockInterview() throws IOException, SQLException {
        int type = Util.readIntInRange("1 = Technical, 2 = HR: ", 1, 2);
        new InterviewService().startMock(student, type == 1 ? "TECH" : "HR");
    }

    private void inbox() throws SQLException {
        Util.title("Notification Inbox");
        notifier.showInbox(student.getId());
        if (Util.readYesNo("Clear inbox?")) {
            notifier.clearInbox(student.getId());
            System.out.println("Inbox cleared.");
        }
    }

    private void jobSecurity() throws SQLException {
        printJobs(jobService.getJobsForStudents());
        Job job = jobService.getJob(Util.readInt("Job ID: "));
        if (job == null) {
            throw new InvalidInputException("Job not found.");
        }
        System.out.println(jobService.securityInfo(job));
    }

    private void ppoTracking() throws SQLException {
        List<PlacementRecord> list = applicationService.getMyPlacements(student.getId());
        if (list.isEmpty()) {
            System.out.println("No selections yet. Records appear here after a company selects you.");
            return;
        }
        Util.title("My selections and PPO status");
        for (PlacementRecord r : list) {
            r.display();
        }
    }

    private void testResults() throws SQLException {
        List<TestResult> list = testResultDAO.findByStudent(student.getId());
        if (list.isEmpty()) {
            System.out.println("No tests taken yet.");
            return;
        }
        for (TestResult t : list) {
            t.display();
        }
    }
}
