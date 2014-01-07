package smtplistener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author timp
 * @since 2013-12-18
 */
public class SmtpListener implements Runnable {

  private static final Logger log_ = LoggerFactory.getLogger(SmtpListener.class);
  private static int arbetraryPortNumberOver1024 = 1616;

  private int port_;
  private boolean listening_;
  private boolean listen_ = true;
  private SMTPSession currentSession_;
  private ServerSocket serverSocket_ = null;
  private Email lastEmailReceived_;

  public SmtpListener() {
    this(arbetraryPortNumberOver1024);
  }

  public SmtpListener(int port) {
    this.port_ = port;
  }

  @Override
  public void run() {
    try {
      serverSocket_ = new ServerSocket(port_);
      String smtpListenerName = "smtplistener."
          + InetAddress.getLocalHost().getHostName();
      while (listen_) {
        setListening(true);
        log_.debug(LogTracker.number(
            "Starting new session:" + smtpListenerName + " on " + serverSocket_));
        if (currentSession_ != null) {
          lastEmailReceived_ = currentSession_.getEmail();
        }
        currentSession_ = new SMTPSession(smtpListenerName, serverSocket_);
        log_.debug(LogTracker.number(currentSession_.toString()));
        new Thread(currentSession_).run();
        log_.debug(LogTracker.number("Finished session"));
      }
      log_.debug(LogTracker.number("Finished serving"));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void startListening() {
    listen_ = true;
    new Thread(this).start();
    while (!isListening()) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }

  public void stopListening() {
    if (listening_) {
      listen_ = false;
      log_.debug(LogTracker.number("Closing socket"));
      try {
        listening_ = false;
        serverSocket_.close();
        Thread.sleep(1);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
      catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    else {
      throw new RuntimeException("Called when not listening");
    }
  }

  public Email getLastEmailReceived() {
    return lastEmailReceived_;
  }

  public boolean isListening() {
    return listening_;
  }

  private void setListening(final boolean listening) {
    this.listening_ = listening;
  }
}
