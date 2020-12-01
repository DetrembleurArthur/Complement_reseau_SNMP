module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.snmp4j;

    opens org.bourgedetrembleur to javafx.fxml;
    exports org.bourgedetrembleur;
}