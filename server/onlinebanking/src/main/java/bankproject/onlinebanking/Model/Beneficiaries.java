package bankproject.onlinebanking.Model;

import java.math.BigInteger;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "beneficiaries")
public class Beneficiaries {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int beneficiaryid;

    private String beneficiaryname;

    private long beneaccountno;

    private String relation;
    
    @ManyToOne
    @JsonBackReference(value = "user-beneficiaries")
    private User user;
    //@JsonBackReference(value = "user-beneficiaries")
}

