package model;

public class Student extends User implements Comparable<Student> {

    private String branch;
    private double cgpa;
    private int backlogs;
    private String skills;

    public Student(int id, String name, String email, String password,
                   String branch, double cgpa, int backlogs, String skills) {
        super(id, name, email, password);
        this.branch = branch;
        this.cgpa = cgpa;
        this.backlogs = backlogs;
        this.skills = skills;
    }

    public String getBranch() { return branch; }
    public double getCgpa() { return cgpa; }
    public int getBacklogs() { return backlogs; }
    public String getSkills() { return skills; }

    public void setBranch(String branch) { this.branch = branch; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
    public void setBacklogs(int backlogs) { this.backlogs = backlogs; }
    public void setSkills(String skills) { this.skills = skills; }

    @Override
    public String getRole() { return "Student"; }

    @Override
    public void display() {
        System.out.println("-------- Student Profile --------");
        System.out.println("ID       : " + id);
        System.out.println("Name     : " + name);
        System.out.println("Email    : " + email);
        System.out.println("Branch   : " + branch);
        System.out.println("CGPA     : " + cgpa);
        System.out.println("Backlogs : " + backlogs);
        System.out.println("Skills   : " + (skills == null || skills.isEmpty() ? "(none)" : skills));
    }

    @Override
    public int compareTo(Student other) {
        return Double.compare(other.cgpa, this.cgpa);
    }

    @Override
    public String toString() {
        return id + "," + name + "," + email + "," + branch + "," + cgpa + "," + backlogs + ","
                + (skills == null ? "" : skills.replace(",", ";"));
    }
}
