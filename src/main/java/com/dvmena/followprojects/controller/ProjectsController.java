package com.dvmena.followprojects.controller;
import com.dvmena.followprojects.model.Project;
import com.dvmena.followprojects.service.ProjectService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class ProjectsController extends Thread implements Initializable {

    private final ProjectService projectService;
    private ObservableList<Project> obList;
    @FXML
    private TableView<Project> table;
    @FXML
    private TableColumn<Project,String> name;
    @FXML
    private TableColumn<Project,String> description;
    @FXML
    private TableColumn<Project, Hyperlink> link;
    @FXML
    private TextField textName;
    @FXML
    private TextField textDescription;

    @FXML
    private TextField textLink;

    private int index;
    public void onSave(ActionEvent event){
        Project project = Project.builder()
                .name(textName.getText())
                .description(textDescription.getText())
                .link(textLink.getText())
                .build();
        projectService.add(project);
        obList.add(project);
        textName.setText("");
        textDescription.setText("");
        textLink.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.obList = FXCollections.observableArrayList(projectService.getAll());
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        link.setCellValueFactory(new PropertyValueFactory<>("link"));
        table.getColumns().add(name);
        table.getColumns().add(description);
        table.getColumns().add(link);
        table.setItems(obList);
        TableView.TableViewSelectionModel<Project> tableViewSelectionModel =
                table.getSelectionModel();

        tableViewSelectionModel.selectedIndexProperty()
                .addListener((observableValue, number, t1) -> {
                    int index = (int)t1;
                    this.index = index;
                    try {
                        loadDetailsPage(obList.get(index));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    //load new window by project details
    public void loadDetailsPage(Project project) throws IOException {
        StackPane root = new StackPane();
        Label title = new Label("Project details");
        title.setStyle("-fx-font-size: 50px;");
        TextField txtName  = new TextField(project.getName());
        txtName.setStyle("-fx-control-inner-background: grey;");
        txtName.setEditable(false);
        TextField txtDescription  = new TextField(project.getDescription());
        txtDescription.setEditable(false);
        txtDescription.setStyle("-fx-control-inner-background: grey;");
        TextField txtLink = new TextField(project.getLink());
        txtLink.setEditable(false);
        txtLink.setStyle("-fx-control-inner-background: grey;");
        Button removeProject = new Button("Remove project");
        Button editProject = new Button("Edit Project");
        Button saveProject = new Button("Save Project");
        saveProject.setDisable(true);
        Label actionLbl = new Label();
        actionLbl.setStyle("-fx-control-inner-background: red;");

        editProject.setOnAction(event -> {
            txtName.setEditable(true);
            txtName.setStyle("-fx-control-inner-background: white;");
            txtDescription.setEditable(true);
            txtDescription.setStyle("-fx-control-inner-background: white;");
            txtLink.setEditable(true);
            txtLink.setStyle("-fx-control-inner-background: white;");
            saveProject.setDisable(false);
        });

        saveProject.setOnAction(event -> {
            Project editedProject = Project.builder()
                    .id(project.getId())
                    .name(txtName.getText())
                    .description(txtDescription.getText())
                    .link(txtLink.getText())
                    .build();
            projectService.add(editedProject);
            txtName.setEditable(false);
            txtDescription.setEditable(false);
            txtLink.setEditable(false);
            this.obList = FXCollections.observableArrayList(projectService.getAll());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Project Updated");
            alert.setHeaderText(project.getName() + " project information has been updated");
            alert.show();
        });
        // open link in browser or display message
        txtLink.setOnAction(event -> {
            if(Desktop.isDesktopSupported()) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(new URI(project.getLink()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Link");
                alert.setHeaderText("I couldn't open the link in the browser for you... you may copy the link");
                alert.setContentText(project.getLink());
                alert.showAndWait();
            }
        });
// add element to vbox
        VBox vbox = new VBox();
        vbox.getChildren().add(title);
        vbox.getChildren().add(txtName);
        vbox.getChildren().add(txtDescription);
        vbox.getChildren().add(txtLink);
        vbox.getChildren().add(removeProject);
        vbox.getChildren().add(editProject);
        vbox.getChildren().add(saveProject);
        vbox.getChildren().add(actionLbl);

        vbox.setAlignment(Pos.CENTER);
        root.getChildren().add(vbox);
        Scene scene = new Scene(root,500,500);

        Stage stage = new Stage();
        Image image = new Image(this.getClass().getResourceAsStream("/com/dvmena/followprojects/images/icon.png"));
        stage.getIcons().add(image);
        stage.setScene(scene);
        stage.show();
        
        removeProject.setOnAction(event -> {
            removeProject.setDisable(true);
            editProject.setDisable(true);
            saveProject.setDisable(true);
            projectService.delete(project.getId());
            this.obList.remove(index);
            actionLbl.setText("Project has been deleted");
        });
    }
}