package com.m2gi.ecom.web.rest;

import com.m2gi.ecom.domain.*;
import com.m2gi.ecom.repository.OrderRepository;
import com.m2gi.ecom.security.SecurityUtils;
import com.m2gi.ecom.service.CartService;
import com.m2gi.ecom.service.OrderService;
import com.m2gi.ecom.service.PromotionService;
import com.m2gi.ecom.service.PromotionalCodeService;
import com.m2gi.ecom.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.m2gi.ecom.domain.Order}.
 */
@RestController
@RequestMapping("/api")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    private static final String ENTITY_NAME = "order";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderService orderService;
    private final CartService cartService;
    private final PromotionService promotionService;
    private final PromotionalCodeService promotionalCodeService;

    private final OrderRepository orderRepository;

    public OrderResource(
        OrderService orderService,
        OrderRepository orderRepository,
        CartService cartService,
        PromotionService promotionService,
        PromotionalCodeService promotionalCodeService
    ) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.promotionService = promotionService;
        this.promotionalCodeService = promotionalCodeService;
    }

    //    /**
    //     * {@code POST  /orders} : Create a new order.
    //     *
    //     * @param order the order to create.
    //     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new order, or with status {@code 400 (Bad Request)} if the order has already an ID.
    //     * @throws URISyntaxException if the Location URI syntax is incorrect.
    //     */
    //    @PostMapping("/orders")
    //    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws URISyntaxException {
    //        log.debug("REST request to save Order : {}", order);
    //        if (order.getId() != null) {
    //            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_NAME, "idexists");
    //        }
    //        Order result = orderService.save(order);
    //        return ResponseEntity
    //            .created(new URI("/api/orders/" + result.getId()))
    //            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
    //            .body(result);
    //    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing order.
     *
     * @param id the id of the order to save.
     * @param order the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated order,
     * or with status {@code 400 (Bad Request)} if the order is not valid,
     * or with status {@code 500 (Internal Server Error)} if the order couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Order order)
        throws URISyntaxException {
        log.debug("REST request to update Order : {}, {}", id, order);
        if (order.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, order.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Order result = orderService.save(order);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing order, field will ignore if it is null
     *
     * @param id the id of the order to save.
     * @param order the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated order,
     * or with status {@code 400 (Bad Request)} if the order is not valid,
     * or with status {@code 404 (Not Found)} if the order is not found,
     * or with status {@code 500 (Internal Server Error)} if the order couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Order> partialUpdateOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Order order
    ) throws URISyntaxException {
        log.debug("REST request to partial update Order partially : {}, {}", id, order);
        if (order.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, order.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Order> result = orderService.partialUpdate(order);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString())
        );
    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        log.debug("REST request to get all Orders");
        return orderService.findAll();
    }

    /**
     * {@code GET  /orders/:id} : get the "id" order.
     *
     * @param id the id of the order to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the order, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        Optional<Order> order = orderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(order);
    }

    /**
     * {@code GET  /orders/user} : get the "id" order.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the order, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/orders/user")
    public List<Order> getOrdersForUser() {
        final String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        log.debug("REST request to get Order for User : {}", login);
        return orderService.findAllByUserLogin(login);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" order.
     *
     * @param id the id of the order to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.debug("REST request to delete Order : {}", id);
        orderService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /orders} : Create a new order.
     *
     * @param order the order to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new order, or with status
     * {@code 400 (Bad Request)} if the order has already an ID or if the user does not have a cart or has an empty one.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrderForAuthenticatedUser(@RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to create an Order for the authenticated User : {}", order);
        if (order.getId() != null) {
            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_NAME, "idexists");
        }

        final Cart cart = cartService.findOneWithEagerRelationshipsByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElse(null);
        PromotionalCode promoCode = null;

        if (cart == null || cart.getLines().isEmpty()) {
            throw new BadRequestAlertException("User does not have a cart or it is empty.", ENTITY_NAME, "nocart");
        }

        final PromotionalCode requestPromoCode = order.getPromotionalCode();
        if (requestPromoCode != null) {
            promoCode = promotionalCodeService.findOne(requestPromoCode.getId()).orElse(null);
            if (promoCode == null) {
                // Does not exist, there's a problem with this request.
                throw new BadRequestAlertException("Promotional code does not exist.", ENTITY_NAME, "wrongpromocode");
            }

            if (
                !promoCode.isActive(Instant.now()) ||
                (
                    requestPromoCode.getUnit() != promoCode.getUnit() ||
                    requestPromoCode.getValue().doubleValue() != promoCode.getValue().doubleValue()
                )
            ) {
                // Does not exist, there's a problem with this request.
                throw new BadRequestAlertException("Promotional code is not active.", ENTITY_NAME, "wrongpromocode");
            }

            promoCode.getProducts().retainAll(order.getLines().stream().map(ProductOrder::getProduct).collect(Collectors.toSet()));
            if (promoCode.getProducts().isEmpty()) {
                // Promotional Code not applicable for any product of this order.
                throw new BadRequestAlertException("Promotional code is not applicable to this order.", ENTITY_NAME, "wrongpromocode");
            }
        }

        final Set<ProductOrder> orderLinesFromCart = cart
            .getLines()
            .stream()
            .map(productCart -> ProductOrder.fromProductCart(productCart).order(order))
            .collect(Collectors.toSet());

        if (!areProductOrdersEquals(orderLinesFromCart, order.getLines())) {
            throw new BadRequestAlertException("Cart has been modified.", ENTITY_NAME, "cartmodified");
        }

        final List<Promotion> promotions = promotionService.findActiveForProducts(
            Instant.now(),
            orderLinesFromCart.stream().map(ProductOrder::getProduct).collect(Collectors.toList())
        );

        order.user(cart.getUser()).paymentDate(Instant.now()).lines(orderLinesFromCart);

        if (
            calculateOrderPrice(order, promoCode, promotions).doubleValue() !=
            order.getTotalPrice().setScale(2, RoundingMode.HALF_UP).doubleValue()
        ) {
            throw new BadRequestAlertException("Incorrect order total price.", ENTITY_NAME, "wrongprice");
        }

        Order result = orderService.createOrder(order, cart);
        return ResponseEntity
            .created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    private boolean areProductOrdersEquals(Set<ProductOrder> set1, Set<ProductOrder> set2) {
        if (set1.size() != set2.size()) return false;

        final List<ProductOrder> l1 = set1.stream().sorted().collect(Collectors.toList());
        final List<ProductOrder> l2 = set2.stream().sorted().collect(Collectors.toList());

        for (int i = 0; i < l1.size(); i++) {
            final ProductOrder o1 = l1.get(i);
            final ProductOrder o2 = l2.get(i);
            if (!Objects.equals(o1.getProduct().getId(), o2.getProduct().getId())) return false;
            if (!Objects.equals(o1.getQuantity(), o2.getQuantity())) return false;
        }

        return true;
    }

    private BigDecimal calculateOrderPrice(Order order, PromotionalCode promoCode, List<Promotion> promotions) {
        BigDecimal total = BigDecimal.ZERO;
        total =
            total.add(
                order
                    .getLines()
                    .stream()
                    .map(po -> {
                        final Optional<Promotion> promotion = promotions
                            .stream()
                            .filter(p -> p.getProducts().contains(po.getProduct()))
                            .findFirst();
                        BigDecimal productBasePrice = po.getProduct().getPrice();
                        if (promotion.isPresent()) {
                            final Promotion promo = promotion.get();
                            productBasePrice = promo.applyTo(productBasePrice);
                            po.promotionType(promo.getUnit()).promotionValue(promo.getValue());
                        }

                        BigDecimal finalUnitPrice = productBasePrice;
                        if (promoCode != null && promoCode.getProducts().contains(po.getProduct())) {
                            finalUnitPrice = promoCode.applyTo(productBasePrice);
                            po.promoCodeType(promoCode.getUnit()).promoCodeValue(promoCode.getValue());
                        }
                        final BigDecimal linePrice = finalUnitPrice.multiply(new BigDecimal(po.getQuantity()));
                        po.setPrice(linePrice.doubleValue() >= 0 ? linePrice : BigDecimal.ZERO);
                        return po.getPrice();
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
