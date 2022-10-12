import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Scanner;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.json.*;
import com.google.zxing.*;

public class FIRSTQR {
    public static void main(String[] args) throws IOException, WriterException {
        // Get data about the competition
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the year of the event");
        int year = sc.nextInt();
        System.out.println("Please enter the ID of the event (https://frc-events.firstinspires.org/)");
        String compID = sc.next();

        // Create a connection to the FIRST API
        URL url = new URL("https://frc-api.firstinspires.org/v3.0/" + year + "/schedule/" +  compID + "?tournamentLevel=Qualification");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Create Authentication variables
        String userCredentials = APICredentials.USERNAME + ":" + APICredentials.API_KEY;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

        // Add headers to the HTTP connection
        connection.setRequestProperty("Authorization", basicAuth);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Language", "en-US");

        // Get response and put it into a JSON Object which is then converted to a JSONArray of matches
        InputStream responseStream = connection.getInputStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        JSONObject JSONResult = new JSONObject(result);
        JSONArray matchArray = (JSONArray) JSONResult.get("Schedule");

        // Create all 6 robot JSONs
        JSONObject red1 = new JSONObject();
        JSONObject red2 = new JSONObject();
        JSONObject red3 = new JSONObject();
        JSONObject blue1 = new JSONObject();
        JSONObject blue2 = new JSONObject();
        JSONObject blue3 = new JSONObject();

        for(int i = 1; i < matchArray.length(); i++){
            JSONObject match = (JSONObject) matchArray.get(i - 1);
            JSONArray teams = (JSONArray) match.get("teams");

            // Add the red 1 robot
            JSONObject R1team = (JSONObject) teams.get(0);
            red1.put("" + match.get("matchNumber"), R1team.get("teamNumber"));

            // Add the red 2 robot
            JSONObject R2team = (JSONObject) teams.get(1);
            red2.put("" + match.get("matchNumber"), R2team.get("teamNumber"));

            // Add the red 3 robot
            JSONObject R3team = (JSONObject) teams.get(2);
            red3.put("" + match.get("matchNumber"), R3team.get("teamNumber"));

            // Add the blue 1 robot
            JSONObject B1team = (JSONObject) teams.get(3);
            blue1.put("" + match.get("matchNumber"), B1team.get("teamNumber"));

            // Add the blue 2 robot
            JSONObject B2team = (JSONObject) teams.get(4);
            blue2.put("" + match.get("matchNumber"), B2team.get("teamNumber"));

            // Add the blue 3 robot
            JSONObject B3team = (JSONObject) teams.get(5);
            blue3.put("" + match.get("matchNumber"), B3team.get("teamNumber"));
        }
        // Create all 6 QR codes
        BitMatrix bitMatrixR1 = new QRCodeWriter().encode(red1.toString(), BarcodeFormat.QR_CODE, 750, 750);
        BitMatrix bitMatrixR2 = new QRCodeWriter().encode(red2.toString(), BarcodeFormat.QR_CODE, 750, 750);
        BitMatrix bitMatrixR3 = new QRCodeWriter().encode(red3.toString(), BarcodeFormat.QR_CODE, 750, 750);
        BitMatrix bitMatrixB1 = new QRCodeWriter().encode(blue1.toString(), BarcodeFormat.QR_CODE, 750, 750);
        BitMatrix bitMatrixB2 = new QRCodeWriter().encode(blue2.toString(), BarcodeFormat.QR_CODE, 750, 750);
        BitMatrix bitMatrixB3 = new QRCodeWriter().encode(blue3.toString(), BarcodeFormat.QR_CODE, 750, 750);

        // Create and save to all 6 paths using comp id and year
        Path pathR1 = Path.of("R1" + year + compID + ".png");
        Path pathR2 = Path.of("R2" + year + compID + ".png");
        Path pathR3 = Path.of("R3" + year + compID + ".png");
        Path pathB1 = Path.of("B1" + year + compID + ".png");
        Path pathB2 = Path.of("B2" + year + compID + ".png");
        Path pathB3 = Path.of("B3" + year + compID + ".png");

        MatrixToImageWriter.writeToPath(bitMatrixR1, "jpg", pathR1);
        MatrixToImageWriter.writeToPath(bitMatrixR2, "jpg", pathR2);
        MatrixToImageWriter.writeToPath(bitMatrixR3, "jpg", pathR3);
        MatrixToImageWriter.writeToPath(bitMatrixB1, "jpg", pathB1);
        MatrixToImageWriter.writeToPath(bitMatrixB2, "jpg", pathB2);
        MatrixToImageWriter.writeToPath(bitMatrixB3, "jpg", pathB3);
    }
}

