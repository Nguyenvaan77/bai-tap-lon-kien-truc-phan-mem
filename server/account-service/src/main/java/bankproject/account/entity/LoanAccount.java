package bankproject.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
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

    @Column(name = "user_id")
    private String userId;

    @Column(name = "account_no")
    private Long bankAccountNo;
}
