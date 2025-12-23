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
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "bankaccount")
public class BankAccount {

    @Id
    private Long accountno;

    @Column
    private String accountType;

    @Column
    private String dateCreated;

    @Column
    private String timeCreated;
    @Column
    private Double balance;

    private boolean isactive;

    @ManyToOne
    @JsonBackReference(value = "user-account")
    private User user;

    // @OneToOne(mappedBy = "account")
    // @JsonManagedReference
    // private LoanAccount loanAccount;

}

