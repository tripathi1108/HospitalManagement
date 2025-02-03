package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static  final  String url ="jdbc:mysql://localhost:3306/hospital";
    private static  final  String username ="root";
    private static  final  String password ="Harshtripathi@1102";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctors doctors = new Doctors(connection);
            while(true){
                System.out.println("Hospital Management System");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. view Doctor");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        //Add Patient
                        patient.addPatient();
                        System.out.println();

                    case 2:
                        //View Patient
                        patient.viewPatients();
                        System.out.println();

                    case 3:
                        //View Doctor
                        doctors.viewDoctors();
                        System.out.println();

                    case 4:
                        //Book Appointment
                        bookAppointment(connection,scanner,patient,doctors);

                    case 5:
                        return;

                    default:
                        System.out.println("Enter valid choice!!!");
                }



            }
        }

        catch (SQLException e){
            e.printStackTrace();

        }
    }

    public static  void bookAppointment(Connection connection,Scanner scanner,Patient patient,Doctors doctors){
        System.out.println("Enter Patient Id: ");
        int patientId =scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById((patientId)) && doctors.getDoctorById(doctorId))
        {
            if(checkDoctorAvailability(doctorId,appointmentDate,connection))
            {
                String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked!");
                    }
                    else {
                        System.out.println("Failed to Book Appointment");
                    }

                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("Doctor not available on this date!!");
            }
        }
        else {
            System.out.println("Either patient or doctor in not exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId,String appointmentDate,Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_Date=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return  false;
    }

}
