package it.unibz.inf.ontouml.vp.utils;

public class ServerRequest implements Runnable {

  private boolean doStop = false;

  public synchronized void doStop() {
    this.doStop = true;
  }

  protected synchronized boolean keepRunning() {
    return this.doStop == false;
  }

  @Override
  public void run() {}
}
