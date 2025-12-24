package bankproject.account.entity;

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
@Table(name = "transactions")
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int transactionId;

    @Column
    private long fromAccount;

    @Column
    private double senderBal;

    @Column
    private long toAccount;

    @Column
    private double receiverBal;

    @Column
    private Double amount;

    @Column
    private String transactionStatus;

    @Column
    private String transactionDate;

    @Column
    private String transactionTime;

    @Column
    private String description;
}
