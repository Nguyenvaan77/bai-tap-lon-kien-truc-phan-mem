package bankproject.onlinebanking.Model;

import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    // @ColumnTransformer(read = "UPPER(name)", write = "LOWER(?)")
    private String pan;

    private char gender;

    @Column(name = "birthdate")
    public Date dateOfBirth;

    @OneToOne
    @JsonBackReference
    private User user;

    private int age;
}

