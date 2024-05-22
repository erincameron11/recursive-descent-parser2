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


public class Main extends Application {
	public static TextArea errorTA;
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
			title.setStyle("-fx-font-size: 32px");
			BorderPane.setMargin(title, new Insets(20));
			
			// Create a textfield for filename
			Label fileLabel = new Label("Filename: ");
			TextField fileTF = new TextField();
			Text extension = new Text(".txt");
			HBox fileAndExtension = new HBox();
			fileAndExtension.getChildren().addAll(fileTF, extension);
			
			// Create a textarea for code
			Label codeLabel = new Label("Input Code: ");
			TextArea codeTA = new TextArea();
			HBox codeAndCheckmark = new HBox();
			codeAndCheckmark.getChildren().addAll(codeTA, checkmark);
			
			// Create an error log
			Label errorLabel = new Label("Syntax Error Log: ");
			errorTA = new TextArea();
		
			// Add all to a vbox and root
			VBox vbox = new VBox(10);
			vbox.getChildren().addAll(fileLabel, fileAndExtension, codeLabel, codeAndCheckmark, errorLabel, errorTA);
			BorderPane.setMargin(vbox, new Insets(10, 20, 50, 20));
			
			// Create two buttons
			Button submitBtn = new Button("Submit");
			Button clearBtn = new Button("Clear");
			HBox btnHBox = new HBox();
			btnHBox.getChildren().addAll(submitBtn, clearBtn);
			
			// Set the items in the root
			root.setTop(title);
			root.setCenter(vbox);	
			root.setBottom(btnHBox);
			
			// Styling for error text and checkmark
			errorTA.setEditable(false);
			errorTA.setStyle("-fx-text-fill: red");
			checkmark.setStyle("-fx-font-size: 24px");
			checkmark.setVisible(false);
			
			
			// Event handlers
			submitBtn.setOnAction(e -> {
				// Reset the error text, and remove checkmark if it was previously set
				errorTA.setText("");
				errorTA.setStyle("-fx-text-fill: red");
				checkmark.setVisible(false);
				
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
				RecursiveDescentParser rdp = new RecursiveDescentParser(filename);
				
				// Call the recursive descent parser
				if(text == "") {
					// Display an error message
					RecursiveDescentParser.error("File Error", "File \'" + filename + "\' is empty.");
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
