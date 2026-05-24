/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;
import db.koneksi;
import View.Login;

/**
 *
 * @author LENOVO
 */
public class Main {

    public static void main(String[] args) {

        koneksi.getConnection();
        new Login().setVisible(true);

    }   
}
