package com.honeywell.rajendra.airfactFileUploadPauseAndResume.controller;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

@RestController
public class FileUploadController {

	public boolean isFileUploadComplete(){
		
		return true;
	}
	@PostMapping("/fileUploadStart")
   /* @ApiOperation(value = "Make a POST request to upload the file",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The POST call is Successful"),
            @ApiResponse(code = 500, message = "The POST call is Failed"),
            @ApiResponse(code = 404, message = "The API could not be found")
    })*/
	public ResponseEntity<String> fileUploadPauseAndResume(
    @RequestPart("file") MultipartFile file){
		File myFile = new File("test");
		try {
			
	        FileUtils.writeByteArrayToFile(myFile, file.getBytes());
	        List<String> lines = FileUtils.readLines(myFile);
	        lines.forEach(line -> System.out.println(line));
		
		DefaultAWSCredentialsProviderChain credentialProviderChain = new DefaultAWSCredentialsProviderChain();
		 TransferManager tx = new TransferManager(
		                credentialProviderChain.getCredentials());
		 Upload myUpload = tx.upload("aircraftBigFile", myFile.getName(), myFile);

		 
		 if (myUpload.isDone() == false) {
		        System.out.println("Transfer: " + myUpload.getDescription());
		        System.out.println("  - State: " + myUpload.getState());
		        System.out.println("  - Progress: "
		                        + myUpload.getProgress().getBytesTransferred());
		 }

		
		 
		 Transfer transfer;
		 //myUpload.addProgressListener(createProgressListener(transfer));

		 
		 myUpload.waitForCompletion();

		 // After the upload is complete, call shutdownNow to release the resources.
		 tx.shutdownNow();
		} catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
		 return new ResponseEntity<String>("Done", HttpStatus.OK);
	}
	private ProgressListener createProgressListener(final Transfer transfer)
	{
	    return new ProgressListener()
	    {
	        private ProgressEventType previousType;
	        private double previousTransferred;

	        public synchronized void progressChanged(ProgressEvent progressEvent)
	        {
	            ProgressEventType eventType = progressEvent.getEventType();
	            if (previousType != eventType) {
	                //System.out.println("Upload progress event ("+bucket+"/"+ key+"):"+ eventType);
	                previousType = eventType;
	            }

	            double transferred = transfer.getProgress().getPercentTransferred();
	            if (transferred >= (previousTransferred + 10.0)) {
	                //log.debug("Upload percentage (%s/%s): %.0f%%", bucket, key, transferred);
	                previousTransferred = transferred;
	            }
	        }
	    };
	}
	
	@PostMapping("/fileUploadPause")
   /* @ApiOperation(value = "Make a POST request to upload the file and pause",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The POST call is Successful"),
            @ApiResponse(code = 500, message = "The POST call is Failed"),
            @ApiResponse(code = 404, message = "The API could not be found")
    })*/
	public ResponseEntity<String> fileUploadPause(){
		return new ResponseEntity<String>("Done", HttpStatus.OK);
	}
	
	@PostMapping("/fileUploadResume")
    /*@ApiOperation(value = "Make a POST request to upload the file and Resume",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The POST call is Successful"),
            @ApiResponse(code = 500, message = "The POST call is Failed"),
            @ApiResponse(code = 404, message = "The API could not be found")
    })*/
	public ResponseEntity<String> fileUploadResume(){
		return new ResponseEntity<String>("Done", HttpStatus.OK);
	}
}
