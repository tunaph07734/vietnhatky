package com.poly.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Entity
@Table(name = "taikhoan")
public class TaiKhoan {
    @NonNull
    @Size(min = 3, max = 20)
    @Id
    @Column(name = "TenTaiKhoan")
    private String TenTaiKhoan;

    @NonNull
    @Size(min = 3, max = 50)
    @Column(name = "MatKhau")
    private String MatKhau;

    @NonNull
    @Size(min = 3, max = 50)
    @Column(name = "HoTen")
    private String HoTen;

    @NonNull
    @Size(min = 3, max = 50)
    @Column(name = "Email")
    private String Email;

    public TaiKhoan() {

    }

    public TaiKhoan(String TenTaiKhoan, String MatKhau, String HoTen, String Email){
        this.TenTaiKhoan = TenTaiKhoan;
        this.MatKhau = MatKhau;
        this.HoTen = HoTen;
        this.Email = Email;
    }

    @Override
    public String toString(){
        return "TaiKhoan{"+
                "TenTaiKhoan"+TenTaiKhoan+
                ", MatKhau='" + MatKhau + '\'' +
                ", HoTen='" + HoTen + '\'' +
                ", Email='" + Email + '\'' +
                '}';
    }

    public String getTenTaiKhoan() {
        return TenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        TenTaiKhoan = tenTaiKhoan;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String matKhau) {
        MatKhau = matKhau;
    }

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
