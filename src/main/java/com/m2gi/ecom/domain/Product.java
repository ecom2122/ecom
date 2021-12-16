package com.m2gi.ecom.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.m2gi.ecom.domain.enumeration.WeightUnit;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A Product.
 */
@Entity
@Table(name = "product", indexes = { @Index(columnList = "name"), @Index(columnList = "origin"), @Index(columnList = "brand") })
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "origin")
    private String origin;

    @Column(name = "brand")
    private String brand;

    @Column(name = "image_path")
    private String imagePath;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "weight", precision = 21, scale = 2)
    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_unit")
    private WeightUnit weightUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "parent", "children", "associatedProducts" }, allowSetters = true)
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_product__related_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "related_categories_id")
    )
    @JsonIgnoreProperties(value = { "parent", "children", "associatedProducts" }, allowSetters = true)
    private Set<Category> relatedCategories = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rel_product__tags",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "tags_id"),
        indexes = { @Index(columnList = "product_id"), @Index(columnList = "tags_id") }
    )
    @JsonIgnoreProperties(value = { "products", "preferencesOfs" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Set<Recipe> recipes = new HashSet<>();

    /**
     * FIXME: Only created because JHipster needs a bidirectional ManyToMany Relationship, should not be used.
     */
    @ApiModelProperty(value = "FIXME: Only created because JHipster needs a bidirectional ManyToMany Relationship, should not be used.")
    @ManyToMany(mappedBy = "products")
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Set<Promotion> associatedPromotions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public Product quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getVersion() {
        return this.version;
    }

    public Product version(Long version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getOrigin() {
        return this.origin;
    }

    public Product origin(String origin) {
        this.setOrigin(origin);
        return this;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getBrand() {
        return this.brand;
    }

    public Product brand(String brand) {
        this.setBrand(brand);
        return this;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public Product imagePath(String imagePath) {
        this.setImagePath(imagePath);
        return this;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Product price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getWeight() {
        return this.weight;
    }

    public Product weight(BigDecimal weight) {
        this.setWeight(weight);
        return this;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public WeightUnit getWeightUnit() {
        return this.weightUnit;
    }

    public Product weightUnit(WeightUnit weightUnit) {
        this.setWeightUnit(weightUnit);
        return this;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Product category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Set<Category> getRelatedCategories() {
        return this.relatedCategories;
    }

    public void setRelatedCategories(Set<Category> categories) {
        this.relatedCategories = categories;
    }

    public Product relatedCategories(Set<Category> categories) {
        this.setRelatedCategories(categories);
        return this;
    }

    public Product addRelatedCategories(Category category) {
        this.relatedCategories.add(category);
        category.getAssociatedProducts().add(this);
        return this;
    }

    public Product removeRelatedCategories(Category category) {
        this.relatedCategories.remove(category);
        category.getAssociatedProducts().remove(this);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Product tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Product addTags(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public Product removeTags(Tag tag) {
        this.tags.remove(tag);
        return this;
    }

    public Set<Recipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        if (this.recipes != null) {
            this.recipes.forEach(i -> i.removeProducts(this));
        }
        if (recipes != null) {
            recipes.forEach(i -> i.addProducts(this));
        }
        this.recipes = recipes;
    }

    public Product recipes(Set<Recipe> recipes) {
        this.setRecipes(recipes);
        return this;
    }

    public Product addRecipes(Recipe recipe) {
        this.recipes.add(recipe);
        recipe.getProducts().add(this);
        return this;
    }

    public Product removeRecipes(Recipe recipe) {
        this.recipes.remove(recipe);
        recipe.getProducts().remove(this);
        return this;
    }

    public Set<Promotion> getAssociatedPromotions() {
        return this.associatedPromotions;
    }

    public void setAssociatedPromotions(Set<Promotion> promotions) {
        if (this.associatedPromotions != null) {
            this.associatedPromotions.forEach(i -> i.removeProducts(this));
        }
        if (promotions != null) {
            promotions.forEach(i -> i.addProducts(this));
        }
        this.associatedPromotions = promotions;
    }

    public Product associatedPromotions(Set<Promotion> promotions) {
        this.setAssociatedPromotions(promotions);
        return this;
    }

    public Product addAssociatedPromotions(Promotion promotion) {
        this.associatedPromotions.add(promotion);
        promotion.getProducts().add(this);
        return this;
    }

    public Product removeAssociatedPromotions(Promotion promotion) {
        this.associatedPromotions.remove(promotion);
        promotion.getProducts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", quantity=" + getQuantity() +
            ", version=" + getVersion() +
            ", origin='" + getOrigin() + "'" +
            ", brand='" + getBrand() + "'" +
            ", imagePath='" + getImagePath() + "'" +
            ", price=" + getPrice() +
            ", weight=" + getWeight() +
            ", weightUnit='" + getWeightUnit() + "'" +
            "}";
    }
}
