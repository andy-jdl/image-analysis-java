import com.azure.ai.vision.imageanalysis.*;
import com.azure.ai.vision.imageanalysis.models.*;
import com.azure.core.credential.KeyCredential;
import com.azure.core.util.BinaryData;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageAnalysis {
    public static void analyzeRemoteImage(ImageAnalysisClient client) {
        String pathToRemoteImage = "YOUR_VERY_EXPLICIT_URL_IMAGE_HERE";
        List<VisualFeatures> featuresToExtractFromImage = new ArrayList<>();
        featuresToExtractFromImage.add(VisualFeatures.TAGS);
        featuresToExtractFromImage.add(VisualFeatures.CAPTION);
        featuresToExtractFromImage.add(VisualFeatures.DENSE_CAPTIONS);

        System.out.println("\n\nAnalyzing image from url...");
        try{
            URL url = new URL(pathToRemoteImage);
            byte[] imageBytes = url.openStream().readAllBytes();
            BinaryData imageData = BinaryData.fromBytes(imageBytes);

            // NOTE: analyzeFromUrl needs a public url Image.
            // artic.edu (or any institution that host's their images) may block your request
            // Used analyze instead through downloading the image first
            ImageAnalysisOptions options = new ImageAnalysisOptions();
            ImageAnalysisResult result = client.analyze(imageData, featuresToExtractFromImage, options);

            for (DetectedTag tag : result.getTags().getValues()) {
                System.out.printf("%s with confidence %f\n", tag.getName(), tag.getConfidence());
            }

            String captionResult = result.getCaption().getText();
            System.out.printf("Caption Analysis: %s\n", captionResult);

            DenseCaptionsResult denseCaptionResult = result.getDenseCaptions();
            for(DenseCaption ds: denseCaptionResult.getValues()) {
                System.out.printf("Dense caption: %s\n", ds.getText());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        // Set these variables in your preferred shell
         String visionEndpoint = System.getenv("VISION_ENDPOINT");
         String visionKey = System.getenv("VISION_KEY");

        if(visionEndpoint == null || visionKey == null) {
            System.out.println("Missing environment variable 'VISION_ENDPOINT' or 'VISION_KEY'");
            System.exit(1);
        }

        // Blocks until service responds
        // Use Async Image Client for non-blocking
        // Use default entra ID if you don't want to provide a key.
        ImageAnalysisClient client = new ImageAnalysisClientBuilder()
                .endpoint(visionEndpoint)
                .credential(new KeyCredential(visionKey))
                .buildClient();

        analyzeRemoteImage(client);
    }
}