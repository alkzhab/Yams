module Yams {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.base;
	
	opens vue to javafx.graphics, javafx.fxml;
	opens controleur to javafx.fxml;
	opens model to javafx.fxml;
}
