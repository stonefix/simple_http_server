import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;


public class HttpServer implements Runnable {

  protected final Socket socket;
  StringBuilder result = new StringBuilder();
  
  public HttpServer(Socket socket) {
    this.socket = socket;
  }

  public void executeRequest() {
    try (var inputStream = socket.getInputStream();
        var outputStream = socket.getOutputStream()) {
      if (getRequest(inputStream)) { 
        String answer = "Answer from server"; 
        writeResponse(outputStream, answer); 
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void run() {
    executeRequest();
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private boolean getRequest(InputStream inputStream)  {
    try {
    var reader = new BufferedReader(new InputStreamReader(inputStream));
    String line = reader.readLine();
    System.out.println(line);
    if (line.startsWith("GET")) {
        return true;
    } 
    return false;
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false;
  }

  
  private void writeResponse(OutputStream output, String content)  {
    try {
        String length = String.valueOf(content.getBytes("UTF-8").length);
        output.write("HTTP/1.1 200 OK\r\n".getBytes());
        output.write(("Content-Length: " + length + "\r\n").getBytes());
        output.write("Content-Type: text/html\r\n\r\n".getBytes());
        output.write((content).getBytes("UTF-8"));
        output.flush();
        output.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    
  }
}