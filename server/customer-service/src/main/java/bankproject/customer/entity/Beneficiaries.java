package bankproject.customer.entity;

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
@Table(name = "beneficiaries")
public class Beneficiaries {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int beneficiaryid;

    private String beneficiaryname;
    private long beneaccountno;
    private String relation;

    @Column(name = "user_id")
    private String userId;
}
