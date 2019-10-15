import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;

public class GitDelegate {

	private final static String repoUrl = "https://github.com/apache/maven.git";
	
	public static void clone(Path path) {
	        try {
	        	System.out.println("Cloning "+repoUrl+" into "+path.toAbsolutePath());
			    ProcessBuilder pb = new ProcessBuilder("git","clone",repoUrl);
			    pb.directory(path.toFile());	        	
			    Process process = pb.start();
	        	
	            final StringWriter messageWriter = new StringWriter();
	            final StringWriter errorWriter = new StringWriter();

	            Thread outDrainer = new Thread(new Runnable() {
	                public void run() {
	                    try {
	                        IOUtils.copy(process.getInputStream(), messageWriter);
	                    } catch (IOException e) {
	                    }
	                }
	            });

	            Thread errorDrainer = new Thread(new Runnable() {
	                public void run() {
	                    try {
	                        IOUtils.copy(process.getErrorStream(), errorWriter);
	                    } catch (IOException e) {
	                    }
	                }
	            });

	            outDrainer.start();
	            errorDrainer.start();

	            int err = process.waitFor();

	            outDrainer.join();
	            errorDrainer.join();

	            if (err != 0) {
	                throw new RuntimeException("Error during repository clone "+errorWriter.toString());
	            }

	            String message = messageWriter.toString();
				System.out.println("Cloning completed "+message);	
	        } catch (IOException | InterruptedException ex) {
                throw new RuntimeException("Error during repository clone "+ex.getMessage());
	        }	    	
	   
	}

	public static void checkOut(Path path,String branch, String commitHash) {
        try {
        	System.out.println("checkout branch "+branch+ " commit hash "+commitHash);
		    ProcessBuilder pb = new ProcessBuilder("git","checkout",commitHash);
		    pb.directory(path.toFile());	        	
		    Process process = pb.start();
        	
            final StringWriter messageWriter = new StringWriter();
            final StringWriter errorWriter = new StringWriter();

            Thread outDrainer = new Thread(new Runnable() {
                public void run() {
                    try {
                        IOUtils.copy(process.getInputStream(), messageWriter);
                    } catch (IOException e) {
                    }
                }
            });

            Thread errorDrainer = new Thread(new Runnable() {
                public void run() {
                    try {
                        IOUtils.copy(process.getErrorStream(), errorWriter);
                    } catch (IOException e) {
                    }
                }
            });

            outDrainer.start();
            errorDrainer.start();

            int err = process.waitFor();

            outDrainer.join();
            errorDrainer.join();

            if (err != 0) {
                String errorMessage = errorWriter.toString();
                System.out.println("Errror message: "+errorMessage);
                return ;
            }

            String message = messageWriter.toString();
			System.out.println("Completed Cloning "+message);	

        } catch (IOException | InterruptedException ex) {
            System.out.println("Errror message "+ex.getMessage());
        }			
	}
	
	

}
