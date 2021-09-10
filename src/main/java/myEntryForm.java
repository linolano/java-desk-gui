import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;

public class myEntryForm {

    private JPanel rootPanel;
    private JLabel helloLabel;
    private JTextField nameTextField;
    private JButton helloButton;
    private JLabel resLabel;
    private JTable resTable;
    DefaultTableModel model = (DefaultTableModel) resTable.getModel();

    public myEntryForm() {
        helloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameTextFieldString = "Hello " + nameTextField.getText();
                resLabel.setText( nameTextFieldString );
                String url = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY";
                CompletableFuture<JSONObject> response = null;
                try {
                    response = getMyUrl( url );
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                response.thenApply( s -> {
                    try {
                        System.out.println( s.getString("title") );
                        printJsonToList( s );
                    } catch (Exception jsonException) {
                        System.out.println( "Error " + jsonException.toString() );
                        jsonException.printStackTrace();
                    }
                    return s;
                    } );
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("myEntryForm");
        frame.setContentPane(new myEntryForm().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void printJsonToList(JSONObject jsonObject ) throws JSONException {
        Iterator x = jsonObject.keys();
        JSONArray jsonArray = new JSONArray();
        model.addColumn("aa");
        model.addColumn("sss");
        while ( x.hasNext() ) {
            String key = (String) x.next();
            jsonArray.put( jsonObject.get(key) );
            model.addRow(new Object[]{"k", "s"});
        }
        //resTable.setModel(model);
        System.out.println( "Print array" + jsonArray + model );
    }

    public CompletableFuture<JSONObject> getMyUrl(String uri) throws JSONException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        return client.sendAsync( request, HttpResponse.BodyHandlers.ofString() )
                .thenApply( s -> {
                    String entity = s.body();
                    JSONObject o = null;
                    try {
                        o = new JSONObject(entity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return o;
                });
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
