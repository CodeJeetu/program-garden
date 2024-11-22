package com.bookswap.sell.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class PolicyController {

	@PostMapping("/test")
	public String test()
	{
		return "Running policy numbers";
	}
	
    // GET method to read the base64 encoded file from a given path and return the policy number
    @GetMapping("/trackbase64")
    public ResponseEntity<String> trackBase64FromFile() {
        // Step 1: Read the base64 string from the given file path
        String base64String = readBase64FromFile("D:\\document.b64");

        if (base64String != null) {
            // Step 2: Decode the base64 string
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);

            try {
                // Step 3: Load the PDF document from the decoded byte array
                PDDocument document = PDDocument.load(decodedBytes);

                // Step 4: Extract text from the PDF using PDFBox
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);

                // Step 5: Use regex to extract the Policy Number
                String policyNumber = extractPolicyNumber(text);
                if (policyNumber != null) {
                    // Return policy number as response
                    return ResponseEntity.status(HttpStatus.OK).body("Policy Number: " + policyNumber);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Policy Number not found.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the document.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Base64 file not found or invalid.");
        }
    }

    // Method to read the base64 string from a file
    private String readBase64FromFile(String filePath) {
        try {
            // Read the entire file content into a byte array
            File file = new File(filePath);
            byte[] fileContent = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(fileContent);
            }

            // Convert the byte array to a String (base64 encoded content)
            return new String(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to extract the policy number using regex
    private String extractPolicyNumber(String text) {
        // Regex pattern for "Policy No." followed by alphanumeric characters (e.g., CD000269)
      //  Pattern pattern = Pattern.compile("Policy No\\.\\s*([A-Za-z0-9]+)");
    	// Pattern pattern = Pattern.compile("Certificate No:\\s*(\\d+)");
    	Pattern pattern = Pattern.compile("Certificate No:\\s*([A-Za-z0-9_\\-]+)");

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);  // Return the policy number (e.g., CD000269)
        }
        return null;  // Return null if not found
    }
}
