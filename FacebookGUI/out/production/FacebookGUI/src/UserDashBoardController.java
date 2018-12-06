import DAO.FriendsDao;
import DAO.PostsDao;
import DAO.ProfilesDao;
import Models.Friends;
import Models.Posts;
import Models.Profiles;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class UserDashBoardController implements Initializable {

    private String userName;
    private String checkFriend;

    public void initialize(URL location, ResourceBundle resources) {
        udb_table_userName.setCellValueFactory(new PropertyValueFactory<>("Username"));
        udb_table_Posts.setCellValueFactory(new PropertyValueFactory<>("Posts"));
        udb_table_Date.setCellValueFactory(new PropertyValueFactory<>("Date"));

        loadPosts();
    }

    @FXML
    private Button udb_logoutButton;

    @FXML
    private Button udb_settingsButton;

    @FXML
    private Label udb_firstNameLabel;

    @FXML
    private Label udb_lastNameLabel;

    @FXML
    private Label udb_AgeLabel;

    @FXML
    private Label udb_EmailLabel;

    @FXML
    private TextArea udb_statusArea;

    @FXML
    private Button udb_editStatusButton;

    @FXML
    private ListView<String> udb_FriendsListView;

    @FXML
    private Button udb_NewPostButton;

    @FXML
    private TextArea udb_postTextArea;

    @FXML
    private TableView<Posts> udb_PostsTableView;

    @FXML
    private TableColumn<Posts, String> udb_table_userName;

    @FXML
    private TableColumn<Posts, String> udb_table_Posts;

    @FXML
    private TableColumn<Posts, String> udb_table_Date;

    @FXML
    private Button udb_DeletePost;

    @FXML
    private TextField udb_friendsUsername;

    @FXML
    private Button udb_addFriend;


    @FXML
    private void logoutOfApplication(ActionEvent event) throws IOException {
        System.out.println("You have logged out.");
        Parent signUpPageParent = FXMLLoader.load(getClass().getResource("login_page.fxml"));
        Scene signUpPageScene = new Scene(signUpPageParent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - 720) / 2);
        stage.setY((screenBounds.getHeight() - 720) / 2);

        stage.setResizable(false);
        stage.setScene(signUpPageScene);
        stage.setTitle("Login");
        stage.show();

    }

    @FXML
    private void openSettings(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("settings_page.fxml"));
        Parent parent = loader.load();

        SettingsController controller = loader.getController();
        controller.init(userName);

        Stage stage = new Stage();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - 600) / 2);
        stage.setY((screenBounds.getHeight() - 400) / 2);

        stage.setScene(new Scene(parent));
        stage.setResizable(false);
        stage.setTitle("Settings");
        stage.show();
    }

    @FXML
    private void editStatus(){
        String statusMessage = udb_statusArea.getText();

        if(!udb_statusArea.isEditable()){
            udb_statusArea.setEditable(true);
            udb_editStatusButton.setText("Save Status");
            udb_editStatusButton.setLayoutX(1381);
            udb_editStatusButton.setLayoutY(26);
        }else { // Editable is true
            udb_statusArea.setEditable(false);
            udb_editStatusButton.setText("Edit Status");
            udb_editStatusButton.setLayoutX(1389);
            udb_editStatusButton.setLayoutY(26);
            try{
                // Update Database
                ProfilesDao.updateStatus(userName, statusMessage);
            }catch (Exception e){

            }
        }
    }

    @FXML
    private void publishNewPost(){
        String postMessage = udb_postTextArea.getText();
        if(postMessage.length() > 0){
            try{
                // Save to DB
                PostsDao.insertPosts(userName, postMessage);
                // Load to TableView Posts
                loadPosts();
            }catch (Exception e){

            }
        }
    }


    @FXML
    private void visitFriend(MouseEvent click) throws IOException{
        if(click.getClickCount() == 2){
            final int selectIndex = udb_FriendsListView.getSelectionModel().getSelectedIndex();
            if(selectIndex != -1){
                checkFriend = udb_FriendsListView.getSelectionModel().getSelectedItem();
                loadVisitorBoard(click);
            }
        }
    }

    private void loadVisitorBoard(MouseEvent click) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("visitor_dashboard.fxml"));
        Parent parent = loader.load();

        VisitorDashboardController controller = loader.getController();
        controller.init(checkFriend);
        controller.setMyUserName(userName);

        Stage stage = (Stage) ((Node)click.getSource()).getScene().getWindow();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - 1920) / 2);
        stage.setY((screenBounds.getHeight() - 1080) / 2);

        stage.setScene(new Scene(parent));
        stage.setResizable(false);
        stage.setTitle(checkFriend + " Dashboard");
        stage.show();
    }


    /** Add, Delete, Load, Check Friends username**/
    @FXML
    private void deleteFriend(){
        // Delete from GUI
        final int selectIndex = udb_FriendsListView.getSelectionModel().getSelectedIndex();
        if(selectIndex != -1){
            String itemToRemove = udb_FriendsListView.getSelectionModel().getSelectedItem();
            final int newSelectedIndex = (selectIndex == udb_FriendsListView.getItems().size() - 1) ? selectIndex - 1 : selectIndex;
            udb_FriendsListView.getItems().remove(selectIndex);
            udb_FriendsListView.getSelectionModel().select(newSelectedIndex);

            //Delete from DB
            try{
                FriendsDao.deleteFriend(userName, itemToRemove);
            }catch (Exception e){

            }
        }
    }

    @FXML
    private void addFriend(ActionEvent event){
        try{
            if(!udb_friendsUsername.getText().equals("")){
                //Username
                checkFriend = udb_friendsUsername.getText();
                //Get friends profile
                ObservableList<Profiles> prof = ProfilesDao.searchProfiles(checkFriend);
                //If prof > 0, then friendsUsername exists

                if(!udb_FriendsListView.getItems().contains(checkFriend)) {
                    if (prof.size() > 0) {
                        //Insert to database
                        FriendsDao.insertFriend(userName, checkFriend);
                        loadFriends();
                    }
                }
            }
        }catch (Exception e){

        }
    }

    @FXML
    private void checkFriendsUsername(){
        checkFriend = udb_friendsUsername.getText();
        try{
            // Checks if username is in the DB
            ObservableList<Profiles> prof = ProfilesDao.searchProfiles(checkFriend);
            if(prof.size() > 0){
                udb_friendsUsername.setStyle("-fx-border-color: #00FF3C; -fx-text-inner-color: #00FF3C;");
            }else{
                if (udb_friendsUsername.getText().equals("")) {
                    udb_friendsUsername.setStyle("-fx-border-color: #DCDCDC; -fx-text-inner-color: black;");
                }else if(prof.size() == 0){ // Username not taken
                    udb_friendsUsername.setStyle("-fx-border-color: red; -fx-text-inner-color: red;");
                }
            }
        } catch (Exception e){

        }
    }

    private void loadFriends(){
        try{
            //Get FriendsList from DB
            ObservableList<Friends> friendsList = FriendsDao.searchFriends(userName);
            //Insert List into ListView
            for(int i = 0; i < friendsList.size(); i++){
                System.out.println(friendsList.get(i).getFriendUserName());
                if(!udb_FriendsListView.getItems().contains(friendsList.get(i).getFriendUserName())){
                    udb_FriendsListView.getItems().add(friendsList.get(i).getFriendUserName());
                }

            }
        }catch (Exception e){

        }
    }

    private void loadStatus(){
        try{
            String statusMessage = ProfilesDao.searchProfiles(userName).get(0).getStatus();
            udb_statusArea.setText(statusMessage);
        }catch (Exception e){

        }
    }

    private void loadPosts(){

        try{
            System.out.println("Loading Posts");
            ObservableList<Posts> postList = PostsDao.searchPosts(userName);
            for(Posts post: postList){
                udb_PostsTableView.getItems().add(post);
            }
            //udb_PostsTableView.setItems(PostsDao.searchPosts(userName));

        }catch (Exception e){

        }
    }

    //Getters
    private String getFirstNameLabel(){
        return udb_firstNameLabel.getText();
    }

    private String getLastNameLabel(){
        return udb_lastNameLabel.getText();
    }

    private String getAgeLabel(){
        return udb_AgeLabel.getText();
    }

    private String getEmail(){
        return udb_EmailLabel.getText();
    }


    // Setter
    private void setFirstNameLabel(String firstName){
        udb_firstNameLabel.setText(firstName);
    }

    private void setLastNameLabel(String lastName){
        udb_lastNameLabel.setText(lastName);
    }

    private void setAge(String age){
        udb_AgeLabel.setText(age);
    }

    private void setEmail(String email){
        udb_EmailLabel.setText(email);
    }

    // Accesses DB to load information of User
    public void init(String userName){

        this.userName = userName;
        udb_PostsTableView = new TableView<>();
        udb_FriendsListView.setStyle("-fx-font: 18pt 'Arial'");
        udb_PostsTableView.setStyle("-fx-font-size: 18pt");
        try{

            ObservableList<Profiles> prof = ProfilesDao.searchProfiles(userName);
            System.out.println("Running");
            System.out.println(prof.get(0).getFirstName());
            setFirstNameLabel(prof.get(0).getFirstName().toUpperCase());
            setLastNameLabel(prof.get(0).getLastName().toUpperCase());
            setAge(prof.get(0).getAge());
            setEmail(prof.get(0).getEmail().toUpperCase());
            loadFriends();
            loadStatus();
            loadPosts();

        } catch(Exception e) {

        }
    }

}
