package org.nicholas.bookstorevaadin.views.me;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.nicholas.bookstorevaadin.model.Book;
import org.nicholas.bookstorevaadin.model.Cart;
import org.nicholas.bookstorevaadin.model.Order;
import org.nicholas.bookstorevaadin.model.OrderItem;
import org.nicholas.bookstorevaadin.repository.OrderRepository;
import org.nicholas.bookstorevaadin.security.details.StoreUserDetails;
import org.nicholas.bookstorevaadin.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Route(value = "cart")
@PageTitle("Cestino")
//@PermitAll
@RolesAllowed("ROLE_USER")
public class CartView extends FlexLayout {
    private org.nicholas.bookstorevaadin.model.Cart cart;
    private AuthenticationService authenticationService;
    private Button home;
    private FlexLayout cartContent;
    private OrderRepository orderRepository;

    private StoreUserDetails principal;

    public CartView(@Autowired Cart cart, AuthenticationService authenticationService, OrderRepository orderRepository) {
        this.cart = cart;
        this.authenticationService = authenticationService;
        this.orderRepository = orderRepository;

        getElement().getThemeList().add(Lumo.DARK);

        setFlexDirection(FlexDirection.COLUMN);
        setAlignItems(Alignment.CENTER);
//        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        principal = (StoreUserDetails) authenticationService.getCurrentPrincipal();

        H1 header = new H1("Cart:");
        header.getStyle().setMarginTop("20px");

        cartContent = new FlexLayout();
        cartContent.setFlexDirection(FlexDirection.COLUMN);
        cartContent.setAlignItems(Alignment.CENTER);
        cartContent.setWidth("max-content");
        cartContent.getStyle().setMarginTop("30px");

        if (principal!=null) {
            home = new Button("Home", event -> {
                getUI().ifPresent(ui -> ui.navigate("/"));
            });
            home.getStyle().setMarginTop("20px");
            add(header, cartContent, home);

            drawChart(cartContent);
        }


    }

    private void drawChart(FlexComponent content) {
        if (cart.getBookMap().isEmpty()) {
            content.add(new H3(new Span("Cart is empty! "), new Anchor("/books", "Choose something!")));
        } else {
            content.setWidth("70%");
            Grid<Book> cartGrid = new Grid<>(Book.class, false);

//            cartGrid.addColumn(Book::getTitle).setHeader("Book");
            cartGrid.addComponentColumn(book -> new Anchor("/books/" + book.getId(), book.getTitle())).setHeader("Book").setAutoWidth(true);
            cartGrid.addColumn(book -> book.getRecommendedPrice()+" MDL").setHeader("Price");
            cartGrid.addComponentColumn(book -> {
                FlexLayout counter = new FlexLayout();
                counter.setFlexDirection(FlexDirection.ROW);

                Button plus = new Button("+");

                NumberField numberField = new NumberField();
                numberField.setStep(1);
                numberField.setMin(1);
                numberField.setValue(Double.valueOf(cart.getBookMap().get(book)));

                Button minus = new Button("-");

                counter.add(plus, numberField, minus);

                plus.addClickListener(event -> {
                    numberField.setValue(numberField.getValue()+1.0);
                    cart.getBookMap().put(book, cart.getBookMap().get(book)+1);
                });
                minus.addClickListener(event -> {
                    if (numberField.getValue() > 1) {
                        numberField.setValue(numberField.getValue()-1.0);
                        cart.getBookMap().put(book, cart.getBookMap().get(book)-1);
                    } else {
                        cart.getBookMap().remove(book);
                        cartGrid.getDataProvider().refreshAll();
                        if (cartGrid.getDataProvider().size(new Query<>()) == 0) {
                            content.removeAll();
                            drawChart(content);
//                            content.add(new H3(new Span("Cart is empty! "), new Anchor("/books", "Choose something!")));
//                            content.setWidth("max-content");
                        }
                    }
                });

                return counter;
            }).setHeader("Amount").setAutoWidth(true);

            cartGrid.setItems(cart.getBookMap().keySet());
            cartGrid.setAllRowsVisible(true);
//            cartGrid.setHeight("250px");
            cartGrid.setWidth("80%");
            content.setAlignSelf(Alignment.CENTER, cartGrid);
            content.add(cartGrid);
            content.add(new H3(new Anchor("/books", "get more...")));

            Button buy = new Button("Buy");
            content.add(buy);

            buy.addClickListener(event -> {
                Order order = new Order();
                order.setCostumer(principal.getUser().getCostumer());
                order.setOrderDate(new Date());
                cart.getBookMap().keySet().forEach(book -> {
                    OrderItem orderItem = new OrderItem();

                    orderItem.setOrder(order);
                    orderItem.setBook(book);
                    orderItem.setItemAgreedPrice(book.getRecommendedPrice());
                    orderItem.setItemComment("Standard wrap");
                    orderItem.setAmount(cart.getBookMap().get(book));

                    order.getItemList().add(orderItem);
                });
                Double orderValue = order.getItemList().stream().map(orderItem -> orderItem.getItemAgreedPrice()*orderItem.getAmount()).reduce((d1, d2) -> d1+d2).orElse(0.0);

                order.setOrderValue(orderValue);

//                for (Book key : cart.getBookMap().keySet()) {
//                    System.out.println(key + " => " + cart.getBookMap().get(key));
//                }
                cart.getBookMap().clear();
                content.removeAll();
                drawChart(content);

                orderRepository.save(order);
            });
//            System.out.println(cart.getBookMap().keySet());
        }
//        add(new H3(cart.toString()));
    }
}