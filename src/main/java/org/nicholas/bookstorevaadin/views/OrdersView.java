package org.nicholas.bookstorevaadin.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.nicholas.bookstorevaadin.model.BookCategory;
import org.nicholas.bookstorevaadin.model.Costumer;
import org.nicholas.bookstorevaadin.model.Order;
import org.nicholas.bookstorevaadin.repository.CostumerRepository;
import org.nicholas.bookstorevaadin.repository.OrderRepository;
import org.nicholas.bookstorevaadin.security.details.StoreUserDetails;
import org.nicholas.bookstorevaadin.service.AuthenticationService;
import org.nicholas.bookstorevaadin.views.OrderItemsDetail;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Route(value = "orders", layout = MainView.class)
@RolesAllowed({"ROLE_MANAGER", "ROLE_ADMIN"})
public class OrdersView extends FlexLayout {
    private AuthenticationService authenticationService;
    private OrderRepository orderRepository;
    private CostumerRepository costumerRepository;
    private StoreUserDetails principal;
    private FlexLayout ordersList;
    private List<Order> orders;

    public OrdersView(AuthenticationService authenticationService, OrderRepository orderRepository, CostumerRepository costumerRepository) {
        this.orderRepository = orderRepository;
        this.costumerRepository = costumerRepository;
        this.authenticationService = authenticationService;

        getElement().getThemeList().add(Lumo.DARK);
        setSizeFull();
        setFlexDirection(FlexDirection.COLUMN);
        setAlignItems(Alignment.CENTER);

        principal = (StoreUserDetails) authenticationService.getCurrentPrincipal();

        orders = orderRepository.findFullAllOrders();
//        System.out.println(orders);

        H1 header = new H1("Orders:");
        header.getStyle().setMarginTop("20px");

        ordersList = new FlexLayout();
        ordersList.setFlexDirection(FlexDirection.COLUMN);
        ordersList.setAlignItems(Alignment.CENTER);
        ordersList.setWidth("max-content");
        ordersList.getStyle().setMarginTop("30px");

        add(header, ordersList);

        drawOrders(ordersList);
    }

    private void drawOrders(FlexComponent content) {
        if (orders.isEmpty()) {
            content.add(new H3("There's no orders yet!"));
        } else {
            content.setWidth("70%");
            Grid<Order> ordersGrid = new Grid<>(Order.class, false);

            ordersGrid.addComponentColumn(order -> order.getCustomer() != null ? new Anchor("/costumers?filter=" + order.getCustomer().getId(), order.getCustomer().getName()) : new Span("Removed!")).setHeader("Costumer").setAutoWidth(true);
            ordersGrid.addColumn(Order::getOrderDate).setHeader("Order date").setAutoWidth(true);
            ordersGrid.addColumn(order -> order.getOrderValue()+" MDL").setHeader("Order value").setAutoWidth(true);

            ordersGrid.setItemDetailsRenderer(new ComponentRenderer<OrderItemsDetail, Order>(OrderItemsDetail::new, OrderItemsDetail::setOrder));
            ordersGrid.addColumn(LitRenderer.<Order> of("<vaadin-button theme=\"tertiary\" @click=\"${show}\">Toggle details</vaadin-button>").withFunction("show", order -> ordersGrid.setDetailsVisible(order, !ordersGrid.isDetailsVisible(order)))).setHeader("Order items").setAutoWidth(true); //@ - indicates, that it is not just a value - it is a function
            ordersGrid.setDetailsVisibleOnClick(false);

            ordersGrid.setItems(orders);
//            ordersGrid.setHeight("400px");
            ordersGrid.setAllRowsVisible(true);
//            ordersGrid.setWidth("100%");
            content.setAlignSelf(Alignment.CENTER, ordersGrid);
            content.add(ordersGrid);
//            System.out.println(cart.getBookMap().keySet());


            if (!principal.getUser().getRoles().isEmpty() && principal.getUser().getRoles().contains("ROLE_ADMIN")) {
                ordersGrid.addComponentColumn(order -> {
                    Button edit = new Button("Edit");
                    edit.addClickListener(event -> {
                        Dialog editDialogue = new Dialog("Edit Order info");

                        addAddOrEditDialogueLayout(editDialogue, order);

                        editDialogue.open();
                    });
                    return edit;
                }).setHeader("Edit").setAutoWidth(true);

                ordersGrid.addComponentColumn(order -> {
                    Button delete = new Button("Delete");
                    delete.addClickListener(event -> {
                        Dialog deleteDialogue = new Dialog("Delete Order Confirmation");

                        addDeleteDialogueLayout(deleteDialogue, order);

                        deleteDialogue.open();
                    });

                    return delete;
                }).setHeader("Delete").setAutoWidth(true);


                Button add = new Button("Add new");

                add(add);
                add.getStyle().setMarginTop("10px");
                setAlignSelf(Alignment.END, add);

                add.setWidth("max-content");
                add.getStyle().setMarginRight("22%");
                add.getStyle().setPadding("5px 10px");

                add.addClickListener(event -> {
                    Dialog addDialog = new Dialog("Add new Order");

                    addAddOrEditDialogueLayout(addDialog, null);

                    addDialog.open();
                });
            }


        }
//        add(new H3(cart.toString()));
    }

    private void addAddOrEditDialogueLayout(Dialog editDialogue, Order order){
        Binder<Order> binder = new Binder<>();

        DateTimePicker orderDate = new DateTimePicker("Order date: ");
        ComboBox<Costumer> costumers = new ComboBox<>("Costumer: ", costumerRepository.findAll());
        costumers.setItemLabelGenerator(Costumer::getName);
//        NumberField orderValue = = new NumberField("Order value:");

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(JustifyContentMode.CENTER);

        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel");

        buttons.add(save, cancel);

        if (order != null){
            orderDate.setValue(order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            costumers.setValue(order.getCustomer());
            save.setText("Edit!");
        }



        FlexLayout layout = new FlexLayout();
        layout.setFlexDirection(FlexDirection.COLUMN);
        layout.add(orderDate, costumers, buttons);

        editDialogue.add(layout);

        save.addClickListener(event -> {
            binder.forField(orderDate).withValidator(d -> d!=null, "Specify orderDate!")
                    .withValidator(d -> d.compareTo(LocalDateTime.now()) <= 0, "Date must not be greather than today!").bind(order1 -> order1.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), (order1, localDateTime) -> order1.setOrderDate(Date.valueOf(localDateTime.toLocalDate())));
            if (order==null)
                binder.forField(costumers).withValidator(c -> c!=null, "Specify the customer!").bind(Order::getCustomer, Order::setCostumer);

            BinderValidationStatus validation = binder.validate();


            if (!validation.hasErrors()) {
                Order newOrder = new Order(costumers.getValue(), Date.valueOf(orderDate.getValue().toLocalDate()), 0.0);

                if (order!=null) {
                    newOrder.setId(order.getId());
                    order.recalculateOrderValue();
                    if (costumers.getValue() == null) {
                        newOrder.setCostumer(order.getCustomer());
                    }
                }

                orderRepository.save(newOrder);
                editDialogue.close();
                getUI().ifPresent(ui -> ui.getPage().reload());
            }
        });

        cancel.addClickListener(event -> editDialogue.close());
    }

    private void addDeleteDialogueLayout(Dialog dialog, Order order) {
        H3 question = new H3("Are you sure you want to delete this order?");
        question.getStyle().setMarginBottom("20px");

        Button delete = new Button("Delete");
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", event -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(delete, cancel);
        buttons.setJustifyContentMode(JustifyContentMode.BETWEEN);

        dialog.add(question, buttons);

        delete.addClickListener(event -> {
            orderRepository.delete(order);
            dialog.close();
            getUI().ifPresent(ui -> ui.getPage().reload());
        });
    }
}
