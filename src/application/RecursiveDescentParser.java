package application;
	
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class RecursiveDescentParser extends Application {
	public static TextArea errorTA = new TextArea();
	public static Text errorText = new Text();
	public static Text checkmark = new Text("\u2713");
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Recursive Descent Parser");
			
			// Create a title
			Text title = new Text("Recursive Descent Parser");
			
			// Create a textfield for filename
			Label fileLabel = new Label("Filename: ");
			TextField fileTF = new TextField();
			Text extension = new Text(".txt");
			HBox fileAndExtension = new HBox();
			fileAndExtension.getChildren().addAll(fileTF, extension);
//			VBox fileHB = new VBox();
//			fileHB.getChildren().addAll(fileLabel, tf);
			
			// Create a textarea for code
			Label codeLabel = new Label("Input Code: ");
			TextArea codeTA = new TextArea();
			HBox codeAndCheckmark = new HBox();
			codeAndCheckmark.getChildren().addAll(codeTA, checkmark);
//			VBox codeHB = new VBox();
			
			// Create an error log
			Label errorLabel = new Label("Syntax Error Log: ");
//			TextArea errorTA = new TextArea();
			
			VBox vbox = new VBox();
			vbox.getChildren().addAll(fileLabel, fileAndExtension, codeLabel, codeAndCheckmark, errorLabel, errorTA);
			
			// Create two buttons
			Button submitBtn = new Button("Submit");
			Button clearBtn = new Button("Clear");
			HBox btnHBox = new HBox();
			btnHBox.getChildren().addAll(submitBtn, clearBtn);
			
			// Set the items in the root
//			root.setTop(fileHB);
			root.setTop(title);
			root.setCenter(vbox);	
			root.setBottom(btnHBox);
			
			// Styling
//			root.setStyle("-fx-background-color: black");
//			fileLabel.setStyle("-fx-text-fill: white");
//			fileTF.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 5px");
//			fileTF.setPadding(new Insets(10, 0, 10, 10));
//			codeLabel.setStyle("-fx-text-fill: white");
//			codeTA.setStyle("-fx-background-color: black; -fx-text-fill: white");
//			codeTA.setPadding(new Insets(10, 0, 10, 10));
//			errorLabel.setStyle("-fx-text-fill: white");
//			codeTA.setPadding(new Insets(10, 0, 10, 10));
//			submitBtn.setStyle("-fx-color: white");
			errorTA.setEditable(false);
			errorTA.setStyle("-fx-text-fill: red");
			checkmark.setStyle("-fx-font-size: 24px");
			checkmark.setVisible(false);
			
			
			// Event handlers
			submitBtn.setOnAction(e -> {
				// TODO: Clear the errorTA if it was previously set
				errorTA.setText("");
				
				String filename = fileTF.getText() + ".txt";
				String text = codeTA.getText();
				
				// Create new file
				File file = new File(filename);
				
				try {
					// Create a new file (or overwrite existing file)
					file.createNewFile();
						// TODO: ERROR -- alert the person the file already exists
						// Quit the application ?? Or do nothing and make the user try again
						// OR -- just overwrite the file	
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				
				// Write to file
				try (PrintWriter out = new PrintWriter(filename)) {
				    out.println(text);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// Create an instance of the recursive descent parser with the filename entered
				RDP rdp = new RDP(filename);
				
				// Call the recursive descent parser
				if(text == "") {
					// Display an error message
					RDP.error("File \'" + filename + "\' is empty.");
				} else {
					rdp.start();
				}
				
			});
			
			
			clearBtn.setOnAction(e -> {
				// Clear all form fields
				fileTF.setText("");
				codeTA.setText("");
				errorTA.setText("");
				checkmark.setVisible(false);
			});
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
