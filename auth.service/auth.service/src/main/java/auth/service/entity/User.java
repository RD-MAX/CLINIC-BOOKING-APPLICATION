package auth.service.entity;


import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name="username", nullable = false)
    private String username;
    @Column(name="email", nullable = false ,unique = true)
    private String email;
    @Column(name="password", nullable = false)
    private String password;
    @Column(name="role", nullable = false)
    private String role; // ADMIN / DOCTOR / PATIENT


    private boolean accountLocked = false;
    private int failedAttempts = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }
}