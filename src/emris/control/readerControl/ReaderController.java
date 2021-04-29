package emris.control.readerControl;

import emris.control.ControllerHandler;
import emris.control.tableInfo.Reader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReaderController extends ControllerHandler implements Initializable {


    @FXML
    private TableView<Reader> tableView;
    @FXML
    private TableColumn<Reader, Integer> idColumn;
    @FXML
    private TableColumn<Reader, String> nameColumn;
    @FXML
    private TableColumn<Reader, String> surnameColumn;
    @FXML
    private TableColumn<Reader, String> roleType;
    @FXML
    private TableColumn<Reader, Date> lastVisit;
    @FXML
    private TableColumn<Reader, Integer> booksOnHands;
    @FXML
    private TableColumn<Reader, Integer> library;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        roleType.setCellValueFactory(new PropertyValueFactory<>("roleType"));
        lastVisit.setCellValueFactory(new PropertyValueFactory<>("lastVisitTime"));
        booksOnHands.setCellValueFactory(new PropertyValueFactory<>("booksOnHands"));
        library.setCellValueFactory(new PropertyValueFactory<>("library"));
        ContextMenu cm = new ContextMenu();
        MenuItem mi1 = new MenuItem("удалить");
        mi1.setOnAction(event -> {
            Reader selectedReader = tableView.getSelectionModel().getSelectedItem();
            String deleteProcedure = "begin \"delete_reader\"(?); end;";
            try {
                CallableStatement cs = session.getConnection().prepareCall(deleteProcedure);
                cs.setString(1, Integer.toString(selectedReader.getId()));
                cs.executeUpdate();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                refresh_table();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


        });
        MenuItem mi2 = new MenuItem("показать лич инфо");
        MenuItem mi3 = new MenuItem("показать книж инфо");
        MenuItem mi4 = new MenuItem("отмена");

        cm.getItems().add(mi1);
        cm.getItems().add(mi2);
        cm.getItems().add(mi3);
        cm.getItems().add(mi4);

        tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                cm.show(tableView, t.getScreenX(), t.getScreenY());
            }
        });

    }


    @FXML
    private void refreshBtn() throws SQLException {
        refresh_table();
    }

    private void refresh_table() throws SQLException {
        ArrayList<Reader> auxList = new ArrayList<>();
        ResultSet ret = session.executeQuery("select \"reader\".\"id\", \"reader\".\"name\",\"reader\".\"surname\",\"reader\".\"library_id\" ,\"reader_role\".\"role\", \"library\".\"name\" as lib_name from \"reader\",\"reader_role\",\"library\"\n" +
                "WHERE \"reader\".\"role_id\" = \"reader_role\".\"id\" and \"reader\".\"library_id\" = \"library\".\"id\"");
        while (ret.next()) {
            Reader reader = new Reader(ret.getInt("id"), ret.getString("name"), ret.getString("surname"), ret.getString("role"), null, 0, ret.getString("lib_name"));
            ////System.out.println(reader.getId() + reader.getName() +  reader.getSurname() +  reader.getRoleType());
            auxList.add(reader);

        }
        tableView.refresh();

        tableView.setItems(FXCollections.observableArrayList(auxList));

    }

    @FXML
    void createBtn() throws IOException {
        changeScene("/emris/control/readerControl/add_reader_1.fxml");
    }

    @FXML
    void backBtn() throws IOException {
        backToMain();
    }


}
