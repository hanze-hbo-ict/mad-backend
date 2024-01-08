package nl.hanze.se4.automaat.web.rest;

import jakarta.validation.Valid;
import nl.hanze.se4.automaat.domain.Customer;
import nl.hanze.se4.automaat.domain.User;
import nl.hanze.se4.automaat.repository.CustomerRepository;
import nl.hanze.se4.automaat.repository.UserRepository;
import nl.hanze.se4.automaat.service.MailService;
import nl.hanze.se4.automaat.service.UserService;
import nl.hanze.se4.automaat.web.rest.errors.InvalidPasswordException;
import nl.hanze.se4.automaat.web.rest.errors.LoginAlreadyUsedException;
import nl.hanze.se4.automaat.web.rest.vm.ManagedUserVM;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/AM")
public class AMAccountResource extends AccountResource {

    private final UserService userService;

    private final MailService mailService;

    private final CustomerRepository customerRepository;

    public AMAccountResource(
        CustomerRepository customerRepository,
        UserRepository userRepository,
        UserService userService,
        MailService mailService
    ) {
        super(userRepository, userService, mailService);
        this.mailService = mailService;
        this.customerRepository = customerRepository;
        // this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        Customer customer = new Customer();
        customer.setSystemUser(user);
        customer.setFirstName(user.getFirstName());
        customer.setLastName(user.getLastName());
        customerRepository.saveAndFlush(customer);

        mailService.sendActivationEmail(user);
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
