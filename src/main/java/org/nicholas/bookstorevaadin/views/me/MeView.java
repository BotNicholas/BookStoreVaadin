package org.nicholas.bookstorevaadin.views.me;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.annotation.security.PermitAll;
import org.nicholas.bookstorevaadin.exceptions.PasswordFormatException;
import org.nicholas.bookstorevaadin.repository.OrderRepository;
import org.nicholas.bookstorevaadin.security.details.StoreUserDetails;
import org.nicholas.bookstorevaadin.security.models.StoreUser;
import org.nicholas.bookstorevaadin.security.models.dto.StoreUserDTO;
import org.nicholas.bookstorevaadin.security.services.StoreUserService;
import org.nicholas.bookstorevaadin.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Route(value = "/me")
@RouteAlias(value = "/users/me")
@PageTitle("Me")
@PermitAll
public class MeView extends FlexLayout {

//    private OrderRepository orderRepository;
    private AuthenticationService authenticationService;
    StoreUserDetails principal;
    private StoreUserService storeUserService;

    public MeView(/*@Autowired OrderRepository orderRepository,*/ AuthenticationService authenticationService, StoreUserService storeUserService) {
//        this.orderRepository = orderRepository;
        this.authenticationService = authenticationService;
        this.storeUserService = storeUserService;

        principal = (StoreUserDetails) authenticationService.getCurrentPrincipal();

        setSizeFull();
        setFlexDirection(FlexDirection.COLUMN);
        setAlignItems(Alignment.CENTER);
//        setJustifyContentMode(JustifyContentMode.CENTER);

        getElement().getThemeList().add(Lumo.DARK);

        Image image = new Image("/images/users/" + principal.getUser().getImage(), principal.getUser().getImage());
        image.setWidth("200px");
        image.setHeight("200px");
        image.getStyle().setBorderRadius("100%");
        image.getStyle().setMarginTop("20px");
        image.getStyle().setMarginBottom("20px");

        H1 name = new H1("Hello, " + principal.getUser().getUsername() + '!');

        add(image, name);

        VerticalLayout userData = new VerticalLayout();
        userData.getStyle().setBorder("1px dashed");
        userData.getStyle().setBorderRadius("10px");
        userData.setWidth("max-content");
        userData.getStyle().setPadding("10px 20px");
        userData.getStyle().setMargin("10px");

        Span username = new Span("Username: " + principal.getUsername());
        Button passwordRefresh = new Button("Change password");
        passwordRefresh.getStyle().setMargin("auto");
        passwordRefresh.addClickListener(event -> {
            Dialog refreshPasswordDialog = new Dialog("Refresh the password...");

            refreshPasswordDialog.add(getRefreshPasswordDialogLayout(refreshPasswordDialog));

            refreshPasswordDialog.open();
        });

        Button changeUsername = new Button("Change username");
        changeUsername.getStyle().setMargin("auto");

        changeUsername.addClickListener(event -> {
            Dialog changeUsernameDialog = new Dialog("Change Username...");
            changeUsernameDialog.add(getChangeUsernameLayout(changeUsernameDialog));
            changeUsernameDialog.open();
        });


        Span role = new Span("Current role: " + ((List)principal.getAuthorities()).get(0).toString().replace("ROLE_", ""));

        Span costumerLabel = new Span("Costumer: ");
        Anchor costumerLink = new Anchor("/costumers/me", principal.getUser().getCostumer().getName());
        Div costumer = new Div(costumerLabel, costumerLink);

        Span ordersLabel = new Span("Orders: ");
        Anchor ordersLink = new Anchor("/orders/me", "see all the orders");
        Div orders = new Div(ordersLabel, ordersLink);

        userData.add(username, role, costumer);
        if (principal.getUser().getRoles()!=null && !principal.getUser().getRoles().contains("ROLE_MANAGER") && !principal.getUser().getRoles().contains("ROLE_ADMIN"))
            userData.add(orders);
        userData.add(changeUsername, passwordRefresh);

        add(userData);


//        add(new H3(principal.toString()));
//        add(new H3(principal.getUser().getCostumer().toString()));
//        add(new H3(orderRepository.findAllByCostumer(principal.getUser().getCostumer()).toString()));
//            add(new H3(Cart.toString()));


        Button logout = new Button("Logout", event -> {
//            VaadinSession.getCurrent().getSession().invalidate();
            authenticationService.logout();
//            getUI().ifPresent(ui -> ui.navigate("/"));

//            VaadinSession.getCurrent().getSession().
//            UI.getCurrent().getPage().reload();
//            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        logout.getStyle().setMargin("5px");

        Button home = new Button("Home", event -> {
            getUI().ifPresent(ui -> ui.navigate("/"));
        });
        home.getStyle().setMargin("5px");

        add(new Div(logout, home));
    }

    private VerticalLayout getRefreshPasswordDialogLayout(Dialog dialog){
        Binder<StoreUser> binder = new Binder<>(StoreUser.class);

        VerticalLayout verticalLayout = new VerticalLayout(Alignment.STRETCH);

        PasswordField password = new PasswordField("Enter new password: ");
        PasswordField confirmation = new PasswordField("Confirm the password: ");

        Button change = new Button("Change!");
        change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        verticalLayout.getStyle().set("width", "25rem").set("max-width", "100%");
        verticalLayout.add(password, confirmation, change);

        change.addClickListener(event -> {
//            System.out.println(binder.validate());

            binder.forField(password).withValidator(s -> s.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*\\_-]).{8,}$"), "minimal one uppercase and lowercase English letter, + minimal one digit and one special character!")
                            .bind(StoreUser::getPassword, StoreUser::setPassword);
            binder.forField(confirmation).withValidator(s -> s.equals(password.getValue()), "Passwords do not match!")
                    .bind(StoreUser::getPassword, StoreUser::setPassword);

            BinderValidationStatus validationStatus = binder.validate();

            binder.removeBinding(password);
            binder.removeBinding(confirmation);

            if (!validationStatus.hasErrors()) {
                try {
                    storeUserService.updatePasswordFor(password.getValue(), principal.getUser());
                    dialog.close();

                    showSuccess("Parola è cambiato con successo!!");
                } catch (PasswordFormatException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return verticalLayout;
    }

    private VerticalLayout getChangeUsernameLayout(Dialog dialog) {
        VerticalLayout verticalLayout = new VerticalLayout(Alignment.STRETCH);

        TextField newName = new TextField("Enter new name: ");

        Button change = new Button("Change!");
        change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        verticalLayout.getStyle().set("width", "25rem").set("max-width", "100%");
        verticalLayout.add(newName, change);

        change.addClickListener(event -> {
            newName.setErrorMessage("");
            newName.setInvalid(false);

            if (storeUserService.findByUsername(newName.getValue()) != null) {
                newName.setErrorMessage("Sceglie un altro nome!");
                newName.setInvalid(true);
            } else {
                StoreUserDTO currentUser = storeUserService.findByUsername(principal.getUsername());
                currentUser.setUsername(newName.getValue());
                storeUserService.update(currentUser.getId(), currentUser);
                showSuccess("Il nome è cambiato con successo!");
                principal.getUser().setUsername(newName.getValue());
                dialog.close();
            }
        });

        return verticalLayout;
    }

    private void showSuccess(String message){
        Dialog success = new Dialog("Successo!");
        VerticalLayout layout = new VerticalLayout(Alignment.CENTER);
        layout.add(new H4(message));

        Button ok = new Button("Ok", event1 -> {
            success.close();
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(ok);

        success.add(layout);

        success.open();
    }
}
