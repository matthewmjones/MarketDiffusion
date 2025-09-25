module uk.ac.ucl {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    opens uk.ac.ucl to javafx.fxml;
    opens uk.ac.ucl.geo to com.fasterxml.jackson.databind;
    exports uk.ac.ucl;
    exports uk.ac.ucl.util;
    exports uk.ac.ucl.service;
}
