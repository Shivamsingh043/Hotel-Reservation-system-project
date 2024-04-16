package hotelReservationSystem;

import com.mysql.cj.exceptions.ConnectionIsClosedException;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "7645033043";

    public static void main(String[] args) throws Exception{

        try {
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection con = DriverManager.getConnection(url, username, password);
            Statement st = con.createStatement();
            while (true) {
                System.out.println();
                System.out.println("Hotel Reservation System");
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter your choice: ");
                System.out.println("1. Reserve a Room: ");
                System.out.println("2. View Reservation: ");
                System.out.println("3. Get Room number: ");
                System.out.println("4. Update Reservation: ");
                System.out.println("5. Delete Reservation: ");
                System.out.println("0. Exit");

                int choice = sc.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(con, sc, st);
                        break;
                    case 2:
                        viewReservation(con, st);
                        break;
                    case 3:
                        getRoom(con, sc, st);
                        break;
                    case 4:
                        updateReservation(con, sc, st);
                        break;
                    case 5:
                        deleteReservation(con, sc, st);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("invalid input!!");
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    // First method to reserve a room
    private static void reserveRoom(Connection con, Scanner sc, Statement st) {
        System.out.println("Enter guest name: ");
        String guestName = sc.next();
        sc.nextLine();
        System.out.println("Enter room Number: ");
        int roomNo = sc.nextInt();
        System.out.println("Enter contact number: ");
        String contactNo = sc.next();

        String sql = "INSERT INTO reservations (guest_name, room_no, contact_no) " +
                "VALUES ('" + guestName + "', " + roomNo + ", '" + contactNo + "')";;
        try {
            int rowsAffected = st.executeUpdate(sql);
            if (rowsAffected > 0) {
                System.out.println("Reservation Successfull!!!");
            }
            else {
                System.out.println("Registration Unsuccessfull!!!");
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservation(Connection con, Statement st) throws SQLException {
        try {
            String sql = "SELECT id, guest_name, room_no, contact_no, reservation_date FROM reservations;";
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                int reservationId = rs.getInt("id");
                String guestName = rs.getString("guest_name");
                int roomNo = rs.getInt("room_no");
                String contactNo = rs.getString("contact_no");
                String reservationDate = rs.getString("reservation_date");


                System.out.println("customer id is: " + reservationId);
                System.out.println("customer name is: " + guestName);
                System.out.println("room number is: " + roomNo);
                System.out.println("contact number is: " + contactNo);
                System.out.println("reservation date is: " + reservationDate);
                System.out.println();
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void getRoom(Connection con, Scanner sc, Statement st) {
        System.out.println("Enter customer name: ");
        String name = sc.nextLine();
        System.out.println("Enter customer id: ");
        int customerId = sc.nextInt();
        System.out.println("Enter contact no: ");

        String sql = "SELECT room_no FROM reservations" +
                "WHERE id = " + customerId +
                "AND guest_name = " + name + ";";
        try{
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int roomNumber = rs.getInt("room_no");
                System.out.println("Room no of this customer is: " + roomNumber);
            }
            else {
                System.out.println("Room no for this customer not found!!!!");
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static void updateReservation(Connection con, Scanner sc, Statement st) {
        System.out.println("Enter the customer id: ");
        int customerId = sc.nextInt();

        if (!reservationExist(con, customerId)) {
            System.out.println("No reservation is available for this customer id!!");
            return;
        }
            System.out.println("Enter new guest name: ");
            String guestName = sc.next();
            System.out.println("Enter new contact number: ");
            String contactNo = sc.next();
            System.out.println("Enter new room number: ");
            int roomNo = sc.nextInt();

            String sql = "UPDATE reservations SET guest_name = '" + guestName + "', " +
                    "room_no = " + roomNo + ", " +
                    "contact_no = '" + contactNo + "' " +
                    "WHERE id = " + customerId;
            try{
                int rowsAffected = st.executeUpdate(sql);
                if (rowsAffected > 0) {
                    System.out.println("Reservation updated successfully.....");
                }
                else {
                    System.out.println("Reservation update unsuccessfull....");
                }
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    private static void deleteReservation(Connection con, Scanner sc, Statement st) {
        System.out.println("Enter customer id to delete the reservation: ");
        int customerId = sc.nextInt();
        if (!reservationExist(con, customerId)){
            System.out.println("Reservation not found for this id...");
            return;
        }
        String sql = "DELETE FROM reservations WHERE id = " + customerId;
        try {
            int rowsAffected = st.executeUpdate(sql);
            if (rowsAffected > 0) {
                System.out.println("Reservation deleted successfully...");
            }
            else {
                System.out.println("Reservation delete unsuccessfully.....");
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }

    private static boolean reservationExist (Connection con, int reservationId) {
        try {
            String sql = "SELECT id FROM reservations WHERE id = " + reservationId;

            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }

}
