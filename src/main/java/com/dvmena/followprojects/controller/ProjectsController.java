package com.dvmena.followprojects.controller;

import com.dvmena.followprojects.model.Fields;
import com.dvmena.followprojects.model.Project;
import com.dvmena.followprojects.service.ProjectService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import jdk.dynalink.StandardOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class ProjectsController extends Thread implements Initializable {
    TableView.TableViewSelectionModel<Project> tableViewSelectionModel;
    private final ProjectService projectService;
    @FXML
    private Button clear;
    @FXML
    private Button uploadFile;
    @FXML
    private Button save;
    @FXML
    private Button edit;
    @FXML
    private Button update;
    @FXML
    private Button remove;
    @FXML
    private Button download;
    private ObservableList<Project> obList;
    @FXML
    private TableView<Project> table;
    @FXML
    private TableColumn <Project,String>owner;
    @FXML
    private TableColumn<Project,String> name;
    @FXML
    private TableColumn<Project,String> description;
    @FXML
    private TableColumn<Project, String> link;

    @FXML
    private TableColumn<Project, String> fileName;
    @FXML
    private TextField textOwner;
    @FXML
    private TextField textName;
    @FXML
    private TextField textDescription;
    @FXML
    private TextField textLink;
    @FXML
    private Label lblFile;
    @FXML
    private Label actionLbl;
    private File file;
    private FileInputStream fileInputStream;
    private Project project;

    private int selected;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //load projects from data base and insert into the table
        obList = FXCollections.observableArrayList(projectService.getAll());
        owner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        link.setCellValueFactory(new PropertyValueFactory<>("link"));
        table.getColumns().add(owner);
        table.getColumns().add(name);
        table.getColumns().add(description);
        table.getColumns().add(link);
        table.setItems(obList);

        //disable button
        edit.setDisable(true);
        update.setDisable(true);
        remove.setDisable(true);
        download.setDisable(true);


        //load project when selecting item from menu

        tableViewSelectionModel = table.getSelectionModel();
        tableViewSelectionModel.selectedIndexProperty()
                .addListener((observableValue, number, t1) -> {
                    actionLbl.setText("");
                    int index = (int) t1;
                    if(obList.size() > 0 && index>=0) {
                        this.selected = index;
                        project = obList.get(selected);
                        textOwner.setEditable(false);
                        textName.setEditable(false);
                        textDescription.setEditable(false);
                        textLink.setEditable(false);
                        textOwner.setText(project.getOwner());
                        textName.setText(project.getName());
                        textDescription.setText(project.getDescription());
                        textLink.setText(project.getLink());

                        edit.setDisable(false);
                        remove.setDisable(false);
                        if(project.getFileBLob()!=null) {
                            download.setDisable(false);
                        }
                        save.setDisable(true);
                    }
                });
    }
    public void clear(ActionEvent event){
        actionLbl.setText("");
        textOwner.setText("");
        textOwner.setEditable(true);
        textName.setText("");
        textName.setEditable(true);
        textDescription.setText("");
        textDescription.setEditable(true);
        textLink.setText("");
        textLink.setEditable(true);
        remove.setDisable(true);
        download.setDisable(true);
        save.setDisable(false);
    }

    //Upload file
    public void uploadFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(null);
        if(file != null) {
            lblFile.setText(file.getName());
        }
    }
    public void onSave(ActionEvent event) throws IOException {
        byte[] fileBytes = null;
        if(file != null) {
            fileBytes = new FileInputStream(file).readAllBytes();
        }
        try {
            Blob fileBlob = null;
            String fileName = "No File";
            if (fileBytes !=null) {
                 fileBlob = new SerialBlob(fileBytes);
                 fileName = file.getName();
            }
            Project project = Project.builder()
                    .owner(textOwner.getText())
                    .name(textName.getText())
                    .description(textDescription.getText())
                    .link(textLink.getText())
                    .fileName(fileName)
                    .fileBLob(fileBlob)
                    .build();
            String errorField = valid(project);
            if(errorField.equals("")) {
                projectService.add(project);
                obList.add(project);
                textOwner.setText("");
                textName.setText("");
                textDescription.setText("");
                textLink.setText("");
                lblFile.setText("");
                remove.setDisable(true);
                download.setDisable(true);
                file = null;
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Field error");
                alert.setHeaderText("Please fill the missing field");
                alert.setContentText(errorField);
                alert.show();
            }
        }catch(SQLException e){
            throw new RuntimeException();
        }
    }

    public void edit(ActionEvent event) {
        actionLbl.setText("");
        textOwner.setEditable(true);
        textName.setEditable(true);
        textDescription.setEditable(true);
        textLink.setEditable(true);
        update.setDisable(false);
        remove.setDisable(true);
        download.setDisable(true);
    }

    public void update(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(null);
        String fileName = "No file";
        Blob fileBlob = null;

        if(file == null) {
            if(project != null){
                fileBlob = project.getFileBLob();
                fileName = project.getFileName();
            }
        }else{
            byte[] fileBytes = new FileInputStream(file).readAllBytes();
            try {
                fileBlob = new SerialBlob(fileBytes);
                fileName = file.getName();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        Project editedProject = Project.builder()
                .id(project.getId())
                .owner(textOwner.getText())
                .name(textName.getText())
                .description(textDescription.getText())
                .link(textLink.getText())
                .fileName(fileName)
                .fileBLob(fileBlob)
                .build();
        //save the project and update the table
        projectService.add(editedProject);
        obList.set(selected,editedProject);
        table.setItems(obList);

        textOwner.setEditable(false);
        textName.setEditable(false);
        textDescription.setEditable(false);
        textLink.setEditable(false);
        remove.setDisable(true);
        download.setDisable(true);
        update.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Project Updated");
        alert.setHeaderText(project.getName() + " project information has been updated");
        alert.show();
    }

    public void remove(ActionEvent event) {
        download.setDisable(true);
        remove.setDisable(true);
        edit.setDisable(true);
        save.setDisable(true);
        projectService.delete(project.getId());
        //Remove the project and update the table
        obList.remove(project);
        project = null;
        table.setItems(obList);
        actionLbl.setText("Project has been deleted");
    }

    public void download(ActionEvent event){
        long id = obList.get(selected).getId();
        Project project1 = projectService.getOne(id);
        String filename = project1.getFileName();
        Blob fileBlob = project1.getFileBLob();
        if(fileBlob !=null) {
            Path downloadPath = Paths.get(System.getProperty("user.home") + "\\downloads\\" + filename);
            try {
                InputStream inputStream = fileBlob.getBinaryStream();
                Files.copy(inputStream, downloadPath, StandardCopyOption.REPLACE_EXISTING);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("File downloaded");
                alert.setHeaderText("File downloaded to your downloaded folder");
                alert.setContentText(project1.getName());
                alert.show();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String valid(Project project){
        String field = "";
        if(project.getOwner().length() > 0){
            if(project.getName().length() >0){
                if(project.getDescription().length()>0){
                    if(!(project.getLink().length()>0)){
                        field = Fields.LINK.getField();
                    }
                }else {
                    field = Fields.DESCRIPTION.getField();
                }
            }else{
                field = Fields.NAME.getField();
            }
        }else{
            field =  Fields.OWNER.getField();
        }
        return field;
    }
}