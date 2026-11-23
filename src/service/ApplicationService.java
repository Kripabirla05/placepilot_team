package service;

import dao.ApplicationDAO;
import dao.PlacementDAO;
import exception.AppException;
import exception.InvalidInputException;
import java.sql.SQLException;
import java.util.List;
import model.Application;
import model.Internship;
import model.Job;
import model.PlacementRecord;
import model.Student;
import notification.NotificationService;

public class ApplicationService {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final PlacementDAO placementDAO = new PlacementDAO();
    private final JobService jobService = new JobService();
    private final NotificationService notifier = NotificationService.getInstance();

    public void applyForJob(Student student, int jobId)
            throws AppException, SQLException {
        Job job = jobService.getJob(jobId);
        if (job == null || !job.isCompanyVerified()) {
            throw new InvalidInputException("Job not found.");
        }
        jobService.checkOrThrow(student, job);
        if (applicationDAO.alreadyApplied(student.getId(), jobId, 0)) {
            throw new AppException("You already applied for this job.");
        }
        applicationDAO.insert(student.getId(), jobId, 0);
        notifier.send(student.getId(), "You applied for " + job.getRole() + " at " + job.getCompanyName(), 3);
    }

    public void applyForInternship(Student student, int internshipId)
            throws AppException, SQLException {
        Internship in = jobService.getInternship(internshipId);
        if (in == null || !in.isCompanyVerified()) {
            throw new InvalidInputException("Internship not found.");
        }
        jobService.checkOrThrow(student, in);
        if (applicationDAO.alreadyApplied(student.getId(), 0, internshipId)) {
            throw new AppException("You already applied for this internship.");
        }
        applicationDAO.insert(student.getId(), 0, internshipId);
        notifier.send(student.getId(), "You applied for internship " + in.getRole() + " at " + in.getCompanyName(), 3);
    }



    public void changeStatus(int companyId, int applicationId, String status) throws SQLException {
        Application app = null;
        for (Application a : applicationDAO.findByCompany(companyId)) {
            if (a.getId() == applicationId) {
                app = a;
                break;
            }
        }
        if (app == null) {
            throw new InvalidInputException("Application not found for your company.");
        }
        applicationDAO.updateStatus(applicationId, status);

        int priority = status.equals("Rejected") ? 2 : 1;
        notifier.send(app.getStudentId(), "Your application for " + app.getRole() + " at "
                + app.getCompanyName() + " is now: " + status, priority);

        if (status.equals("Selected") && !placementDAO.exists(app.getStudentId(), app.getJobId(), app.getInternshipId())) {
            if (app.isForJob()) {
                Job job = jobService.getJob(app.getJobId());
                placementDAO.insert(app.getStudentId(), app.getJobId(), 0, job.getLpa(), "N/A");
            } else {
                placementDAO.insert(app.getStudentId(), 0, app.getInternshipId(), 0, "Pending");
            }
        }
    }

    public List<PlacementRecord> getInternshipRecordsForCompany(int companyId) throws SQLException {
        return placementDAO.findInternshipsByCompany(companyId);
    }

    public List<PlacementRecord> getMyPlacements(int studentId) throws SQLException {
        return placementDAO.findByStudent(studentId);
    }

    public List<Application> getCompanyApplications(int companyId) throws SQLException {
    return applicationDAO.findByCompany(companyId);
}

    public void updatePpo(int companyId, int placementId, String ppoStatus) throws SQLException {
        PlacementRecord found = null;
       
        if (found == null) {
            throw new InvalidInputException("Internship record not found for your company.");
        }
    }
    public List<Application> getMyApplications(int studentId) throws SQLException {
    return new ApplicationDAO().findByStudent(studentId);
}
}
