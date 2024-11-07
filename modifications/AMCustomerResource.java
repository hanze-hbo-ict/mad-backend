package nl.hanze.se4.automaat.web.rest;

import java.util.Optional;
import nl.hanze.se4.automaat.domain.Customer;
import nl.hanze.se4.automaat.domain.User;
import nl.hanze.se4.automaat.repository.AMCustomerRepository;
import nl.hanze.se4.automaat.repository.UserRepository;
import nl.hanze.se4.automaat.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Customer}.
 */
@RestController
@RequestMapping("/api/AM")
@Transactional
public class AMCustomerResource {

    private final Logger log = LoggerFactory.getLogger(AMCustomerResource.class);

    private final AMCustomerRepository customerRepository;
    private final UserRepository userRepository;

    public AMCustomerResource(AMCustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code GET  /me/} : get the customer connected to the currently logged in {@link User).
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/me")
    public ResponseEntity<Customer> getCurrentCustomer() {
        log.debug("REST request to get the logged in Customer");
        Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElse(null));
        Optional<Customer> customer = customerRepository.findOneWithUserId(user.isPresent() ? user.get().getId() : "");
        return ResponseUtil.wrapOrNotFound(customer);
    }
}
