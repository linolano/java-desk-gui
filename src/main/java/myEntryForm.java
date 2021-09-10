import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class myEntryForm {

    private JPanel rootPanel;
    private JLabel helloLabel;
    private JTextField nameTextField;
    private JButton helloButton;
    private JLabel resLabel;
    private JTable resTable;
    private JLabel resLabelCaption;
    DefaultTableModel model = (DefaultTableModel) resTable.getModel();

    public myEntryForm() {
        model.addColumn("key");
        model.addColumn("value");
        helloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameTextFieldString = nameTextField.getText();
                resLabel.setText( nameTextFieldString );
                resLabel.setForeground(Color.BLUE.darker());
                resLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                resLabelCaption.setText("Fetched ");
                String url = nameTextField.getText();
                CompletableFuture<JSONObject> response = null;
                try {
                    response = getMyUrl( url );
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                response.thenApply( s -> {
                    try {
                        printJsonToTable( s );
                    } catch (Exception jsonException) {
                        jsonException.printStackTrace();
                    }
                    return s;
                    } );
            }
        });
        resLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    Desktop.getDesktop().browse(new URI(resLabel.getText()));
                } catch (URISyntaxException | IOException e1) {
                    e1.printStackTrace();
                }
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

    public void printJsonToTable( JSONObject jsonObject ) throws JSONException {
        Iterator x = jsonObject.keys();
        int countRow = 0;
        model.setRowCount( countRow );
        while ( x.hasNext() ) {
            String key = (String) x.next();
            model.insertRow( countRow, new Object[]{ key, jsonObject.get(key)} );
            countRow++;
        }
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

}
