package bankproject.onlinebanking.Model;

import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "userdata")
public class User implements UserDetails {

    @Id
    @Column(name = "userid")
    private String userId;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    public Role role;

    @Column(name = "accountreq")
    private boolean accountopenningreq = false;

    private boolean locked = false;
    private boolean enabled = true;

    @Column(name = "createdate")
    private Date createdDate;

    @Column(name = "resetPasswordToken")
    private String resetPasswordToken;

    @Column
    private boolean emailVerified;

    @Column(name = "otp")
    public String otp;

    @Column(name = "user_image_name")
    private String imageName;

    @Transient
    public String token;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private UserDetail userdetails;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_userid", referencedColumnName = "userid")
    private List<BankAccount> accounts;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_userid", referencedColumnName = "userid")
    private List<Beneficiaries> beneficiaries;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);

        // return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // @JsonManagedReference(value = "user-loan")
    // private List<LoanAccount> loanAccounts;

    // user active we can add in in future

}

