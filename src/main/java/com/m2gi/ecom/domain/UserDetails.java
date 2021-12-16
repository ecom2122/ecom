package com.m2gi.ecom.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.m2gi.ecom.domain.enumeration.Role;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A UserDetails.
 */
@Entity
@Table(name = "user_details")
public class UserDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Address address;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @JsonIgnoreProperties(value = { "lines", "user" }, allowSetters = true)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
    private Cart cart;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "rel_user_details__favorites",
        joinColumns = @JoinColumn(name = "user_details_id"),
        inverseJoinColumns = @JoinColumn(name = "favorites_id")
    )
    @JsonIgnoreProperties(
        value = {
            "category", "relatedCategories", "tags", "recipes", "associatedPromotions", "associatedPromotionalCodes", "favoritesOfs",
        },
        allowSetters = true
    )
    private Set<Product> favorites = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "rel_user_details__preferences",
        joinColumns = @JoinColumn(name = "user_details_id"),
        inverseJoinColumns = @JoinColumn(name = "preferences_id")
    )
    @JsonIgnoreProperties(value = { "products", "preferencesOfs" }, allowSetters = true)
    private Set<Tag> preferences = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserDetails id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return this.role;
    }

    public UserDetails role(Role role) {
        this.setRole(role);
        return this;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public UserDetails birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public UserDetails phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public UserDetails address(Address address) {
        this.setAddress(address);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserDetails user(User user) {
        this.setUser(user);
        return this;
    }

    public Cart getCart() {
        return this.cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public UserDetails cart(Cart cart) {
        this.setCart(cart);
        return this;
    }

    public Set<Product> getFavorites() {
        return this.favorites;
    }

    public void setFavorites(Set<Product> products) {
        this.favorites = products;
    }

    public UserDetails favorites(Set<Product> products) {
        this.setFavorites(products);
        return this;
    }

    public UserDetails addFavorites(Product product) {
        this.favorites.add(product);
        return this;
    }

    public UserDetails removeFavorites(Product product) {
        this.favorites.remove(product);
        return this;
    }

    public Set<Tag> getPreferences() {
        return this.preferences;
    }

    public void setPreferences(Set<Tag> tags) {
        this.preferences = tags;
    }

    public UserDetails preferences(Set<Tag> tags) {
        this.setPreferences(tags);
        return this;
    }

    public UserDetails addPreferences(Tag tag) {
        this.preferences.add(tag);
        tag.getPreferencesOfs().add(this);
        return this;
    }

    public UserDetails removePreferences(Tag tag) {
        this.preferences.remove(tag);
        tag.getPreferencesOfs().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDetails)) {
            return false;
        }
        return id != null && id.equals(((UserDetails) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDetails{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            "}";
    }
}
