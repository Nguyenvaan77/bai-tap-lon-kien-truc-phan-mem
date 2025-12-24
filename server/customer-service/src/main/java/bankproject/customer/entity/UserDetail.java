package bankproject.customer.entity;

import java.sql.Blob;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "userdetails")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userdetailsid;

    private Blob image;
    private String address;
    private String city;
    private String pin;
    private String state;
    private String adhaar;
    private String mobile;
    private String pan;
    private char gender;

    @Column(name = "birthdate")
    private Date dateOfBirth;

    private int age;

    @Column(name = "user_id")
    private String userId;
}
