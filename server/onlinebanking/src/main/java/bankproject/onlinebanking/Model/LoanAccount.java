package bankproject.onlinebanking.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

//@Entity
@Getter
@Setter
@Table(name = "loanaccount")
public class LoanAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long loanaccountno;
    @Column
    private Double remainingAmount;
    @Column
    private Double loanAmount;
    @Column
    private String loanPurpose;
    @Column
    private Double interest;
    @Column
    private int tenureInMonths;
    @Column
    private int monthlyEMI;

    @ManyToOne
    @JsonBackReference(value = "user-loan")
    private User user;

    @OneToOne
    @JsonBackReference
    private BankAccount bankAccount;
}

