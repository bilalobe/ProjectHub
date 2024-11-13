package com.projecthub;

public class Submission {
    private int projectId;
    private int studentId;
    private String filePath;
    private double grade;

    public Submission(int projectId, int studentId, String filePath, double grade) {
        this.projectId = projectId;
        this.studentId = studentId;
        this.filePath = filePath;
        this.grade = grade;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}