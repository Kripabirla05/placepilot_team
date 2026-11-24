package service;

import dao.UserDAO;
import dao.CompanyDAO;
import exception.AppException;
import exception.InvalidInputException;
import java.sql.SQLException;
import java.util.List;
import model.Company;
import model.Recruiter;
import model.Student;
import utility.Util;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    public void registerStudent(String name, String email, String password, String branch,
                                double cgpa, int backlogs, String skills)
            throws AppException, SQLException {

        if (!Util.isValidName(name)) {
            throw new InvalidInputException("Name must have letters only (min 2 characters).");
        }
        if (!Util.isValidEmail(email)) {
            throw new InvalidInputException("Email is not valid.");
        }
        if (!Util.isValidPassword(password)) {
            throw new InvalidInputException("Password needs at least 6 characters and one digit.");
        }
        if (Util.isEmpty(branch)) {
            throw new InvalidInputException("Branch can not be empty.");
        }
        if (!Util.isValidCgpa(cgpa)) {
            throw new InvalidInputException("CGPA must be between 0 and 10.");
        }
        if (!Util.isValidBacklogs(backlogs)) {
            throw new InvalidInputException("Backlogs must be between 0 and 20.");
        }

        String cleanEmail = email.trim().toLowerCase();
        if (userDAO.emailExists(cleanEmail)) {
            throw new AppException("A student with this email already exists.");
        }

        Student s = new Student(0, name.trim(), cleanEmail, password, branch.trim().toUpperCase(),
                cgpa, backlogs, Util.cleanSkills(skills));
        userDAO.insert(s);
    }

    public Student loginStudent(String email, String password) throws AppException, SQLException {
        Student s = userDAO.findByEmailAndPassword(email.trim().toLowerCase(), password);
        if (s == null) {
            throw new AppException("Wrong email or password.");
        }
        return s;
    }

    public Recruiter loginRecruiter(String email, String password) throws AppException, SQLException {
        Recruiter r = companyDAO.findRecruiter(email.trim().toLowerCase(), password);
        if (r == null) {
            throw new AppException("Wrong recruiter email or password.");
        }
        return r;
    }

    public void loginAdmin(String username, String password) throws AppException, SQLException {
        if (!userDAO.checkAdminLogin(username, password)) {
            throw new AppException("Wrong admin username or password.");
        }
    }


    public Student refresh(Student s) throws SQLException {
        Student fresh = userDAO.findById(s.getId());
        return fresh == null ? s : fresh;
    }

    public void updateAcademicDetails(Student s, String branch, double cgpa, int backlogs) throws SQLException {
        if (Util.isEmpty(branch)) {
            throw new InvalidInputException("Branch can not be empty.");
        }
        if (!Util.isValidCgpa(cgpa)) {
            throw new InvalidInputException("CGPA must be between 0 and 10.");
        }
        if (!Util.isValidBacklogs(backlogs)) {
            throw new InvalidInputException("Backlogs must be between 0 and 20.");
        }
        s.setBranch(branch.trim().toUpperCase());
        s.setCgpa(cgpa);
        s.setBacklogs(backlogs);
        userDAO.updateProfile(s);
    }

    public void updateName(Student s, String name) throws SQLException {
        if (!Util.isValidName(name)) {
            throw new InvalidInputException("Name must have letters only (min 2 characters).");
        }
        s.setName(name.trim());
        userDAO.updateProfile(s);
    }

    public void addSkills(Student s, String newSkills) throws SQLException {
        if (Util.isEmpty(newSkills)) {
            throw new InvalidInputException("Please type at least one skill.");
        }
        String all = (s.getSkills() == null ? "" : s.getSkills()) + "," + newSkills;
        s.setSkills(Util.cleanSkills(all));
        userDAO.updateProfile(s);
    }

    public void replaceSkills(Student s, String skills) throws SQLException {
        s.setSkills(Util.cleanSkills(skills));
        userDAO.updateProfile(s);
    }


    public void registerCompany(String companyName, String location, String recruiterName,
                                String email, String password) throws AppException, SQLException {
        if (Util.isEmpty(companyName) || Util.isEmpty(location)) {
            throw new InvalidInputException("Company name and location are required.");
        }
        if (!Util.isValidName(recruiterName)) {
            throw new InvalidInputException("Recruiter name must have letters only.");
        }
        if (!Util.isValidEmail(email)) {
            throw new InvalidInputException("Email is not valid.");
        }
        if (!Util.isValidPassword(password)) {
            throw new InvalidInputException("Password needs at least 6 characters and one digit.");
        }
        if (companyDAO.companyNameExists(companyName.trim())) {
            throw new AppException("Company already registered.");
        }
        if (companyDAO.recruiterEmailExists(email.trim().toLowerCase())) {
            throw new AppException("Recruiter email already used.");
        }
        int companyId = companyDAO.insertCompany(companyName.trim(), location.trim());
        companyDAO.insertRecruiter(recruiterName.trim(), email.trim().toLowerCase(), password, companyId);
    }

    public Company getCompany(int id) throws SQLException {
        return companyDAO.findById(id);
    }

    public List<Company> getAllCompanies() throws SQLException {
        return companyDAO.findAll();
    }

    public List<Company> getUnverifiedCompanies() throws SQLException {
        return companyDAO.findUnverified();
    }

    public boolean verifyCompany(int companyId) throws SQLException {
        return companyDAO.verify(companyId) > 0;
    }
}
