package org.nicholas.bookstorevaadin.views.authentication;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import org.hibernate.validator.HibernateValidator;
import org.nicholas.bookstorevaadin.exceptions.ObjectAlreadyPresentException;
import org.nicholas.bookstorevaadin.mapper.abstraction.AbstractMapperImpl;
import org.nicholas.bookstorevaadin.security.details.StoreUserDetails;
import org.nicholas.bookstorevaadin.security.models.StoreUser;
import org.nicholas.bookstorevaadin.security.models.dto.StoreRegistrationUserDTO;
import org.nicholas.bookstorevaadin.security.services.StoreUserService;
import org.nicholas.bookstorevaadin.utils.ApplicationContext;
import org.reflections.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.thymeleaf.util.StringUtils;

import java.sql.SQLIntegrityConstraintViolationException;


@Route("/register")
@AnonymousAllowed
@CssImport("my-styles/style.css")
@PageTitle("Registration | BookStore")
public class RegistrationView extends FlexLayout {
    private BeanValidationBinder<StoreUser> validationBinder = new BeanValidationBinder<>(StoreUser.class);
    private PasswordEncoder encoder;
    private StoreUserService userService;
    private AbstractMapperImpl mapper;
//    private BCryptPasswordEncoder encoder = ApplicationContext.getContext().getBean("encoder", BCryptPasswordEncoder.class);
//    private StoreUserService userService = ApplicationContext.getContext().getBean("storeUserService", StoreUserService.class);
//    private AbstractMapperImpl mapper = ApplicationContext.getContext().getBean("abstractMapperImpl", AbstractMapperImpl.class);

    Span usedError;

    public RegistrationView(@Autowired PasswordEncoder encoder, @Autowired StoreUserService userService, @Autowired AbstractMapperImpl mapper) {
        this.encoder = encoder;
        this.userService = userService;
        this.mapper = mapper;

        getElement().getThemeList().add(Lumo.DARK);
        setSizeFull();
        setFlexDirection(FlexDirection.COLUMN);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        FlexLayout mainLayout = new FlexLayout();
        mainLayout.setFlexDirection(FlexDirection.COLUMN);
        mainLayout.getStyle().setPadding("40px 20px");
        mainLayout.getStyle().setBackgroundColor("#2c3d52");
        mainLayout.setWidth("20%");


        H2 header = new H2("Registration:");
        header.getStyle().setMarginBottom("2%");

        usedError = new Span("This user can not be used");
        usedError.setClassName("error");
        usedError.setVisible(false);


        TextField username = new TextField("Username: ");
        validationBinder.forField(username).withValidator(s -> !StringUtils.isEmpty(s), "Specify the username!").bind(StoreUser::getUsername, StoreUser::setUsername);

        PasswordField password = new PasswordField("Password: ");
        PasswordField passwordConfirmation = new PasswordField("Confirm password: ");
        validationBinder.forField(password).withValidator(s -> s.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*\\_-]).{8,}$"), "minimal one uppercase and lowercase English letter, + minimal one digit and one special character!")
                .withValidator(s -> s.equals(passwordConfirmation.getValue()), "Passwords do not match!").bind(StoreUser::getPassword, StoreUser::setPassword);

        Button register = new Button("Register!");
//        register.getThemeNames().add(Lumo.DARK);
        register.getStyle().setMarginTop("15%");
//        register.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("min-content");
        formLayout.setWidth("100%");
        formLayout.add(username, password, passwordConfirmation);

        mainLayout.add(header, usedError, formLayout, register);
        add(mainLayout);

        setAlignSelf(Alignment.CENTER, formLayout);



        register.addClickListener(event -> {
            usedError.setVisible(false);
            System.out.println("SAVE ATTEMPT");
            StoreUser user = new StoreUser();
            try {
                validationBinder.writeBean(user);
                user.setPassword(encoder.encode(user.getPassword()));
                userService.register(mapper.toDTO(user, StoreRegistrationUserDTO.class));

                event.getSource().getUI().ifPresent(ui -> ui.navigate("/login"));
            } catch (ValidationException e) {
//                System.out.println(e.getFieldValidationErrors());
                throw new RuntimeException(e);
            } catch (ObjectAlreadyPresentException e) {
                usedError.setVisible(true);
            }
        });
    }
}
