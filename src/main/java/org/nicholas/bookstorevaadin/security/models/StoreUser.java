package org.nicholas.bookstorevaadin.security.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.nicholas.bookstorevaadin.model.Costumer;

@Entity
@Table(name = "users")
public class StoreUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "Specify the username!")
    private String username;

    @NotEmpty(message = "Specify password!")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*\\_-]).{8,}$",
            message = "Minimum 8 characters in length, at least one uppercase and one lowercase English letter, " +
                    "at least one digit and at least one special character!")
    @Length(min = 8, message = "Minimal length is 8!")
    private String password;

    @NotEmpty
    private String roles;
    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Costumer costumer;
    private String image;


    public StoreUser() {
        this.image = "Placeholder.png";
    }

    public StoreUser(String username, String password, String roles, Costumer costumer) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.costumer = costumer;
        this.image = "Placeholder.png";
    }

    public StoreUser(Long id, String username, String password, String roles, Costumer costumer) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.costumer = costumer;
        this.image = "Placeholder.png";
    }

    public StoreUser(Long id, String username, String password, String roles, Costumer costumer, String image) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.costumer = costumer;
        this.image = image;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Costumer getCostumer() {
        return costumer;
    }

    public void setCostumer(Costumer costumer) {
        this.costumer = costumer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                ", costumer=" + costumer +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            StoreUser user = (StoreUser) obj;
            return this.id.equals(user.getId());
        }
        return false;
    }
}
