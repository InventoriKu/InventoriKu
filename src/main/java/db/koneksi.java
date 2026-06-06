/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
 
/**
 *
 * @author LENOVO
 */
public class koneksi {

    private static Connection conn;

    public static Connection getConnection() {

        try {

            String url = "jdbc:mysql://localhost:3306/InventoriKu";
            String user = "root";
            String password = "";

            conn = DriverManager.getConnection(
                    url, user, password
            );

            System.out.println("Koneksi Berhasil!");

            createTable();

        } catch (SQLException e) {
            System.out.println("Koneksi Gagal!");
            System.out.println(e.getMessage());
        }

        return conn;
    }

    private static void createTable() {

        try {

            Statement stmt = conn.createStatement();

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id_user INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    nama_lengkap VARCHAR(100) NOT NULL,
                    role ENUM('ADMIN', 'STAFF') DEFAULT 'STAFF',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS kategori (
                    id_kategori INT AUTO_INCREMENT PRIMARY KEY,
                    nama_kategori VARCHAR(100) NOT NULL
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS barang (
                    id_barang INT AUTO_INCREMENT PRIMARY KEY,
                    nama_barang VARCHAR(100) NOT NULL,
                    id_kategori INT,
                    stok INT DEFAULT 0,
                    stok_minimum INT DEFAULT 5,
                    supplier VARCHAR(100),
                    FOREIGN KEY (id_kategori)
                    REFERENCES kategori(id_kategori)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS stok_masuk (
                    id_masuk INT AUTO_INCREMENT PRIMARY KEY,
                    id_barang INT,
                    id_user INT,
                    jumlah INT NOT NULL,
                    supplier VARCHAR(100),
                    tanggal TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    catatan VARCHAR(100),
                    FOREIGN KEY (id_barang)
                    REFERENCES barang(id_barang),
                    FOREIGN KEY (id_user)
                    REFERENCES users(id_user)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS stok_keluar (
                    id_keluar INT AUTO_INCREMENT PRIMARY KEY,
                    id_barang INT,
                    id_user INT,
                    jumlah INT NOT NULL,
                    departemen VARCHAR(100),
                    catatan VARCHAR(100),                   
                    tanggal TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (id_barang)
                    REFERENCES barang(id_barang),
                    FOREIGN KEY (id_user)
                    REFERENCES users(id_user)
                )
            """);
            
            stmt.executeUpdate("""
                INSERT IGNORE INTO users
                (id_user, username, password, nama_lengkap, role)
                VALUES
                (1, 'admin', 'admin123', 'Administrator', 'ADMIN')
            """);

            System.out.println("Tabel berhasil dibuat!");

        } catch (SQLException e) {
            System.out.println("Gagal membuat tabel!");
            e.printStackTrace();
        }
    }
}
