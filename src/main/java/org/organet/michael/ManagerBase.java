package org.organet.michael;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.ReflectionName;
import org.organet.michael.Connectivity.Node;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ManagerBase {
  private final Logger logger = LogManager.getLogger(this.getClass().getName());

  public void handleMessage(Node node, AdhocMessage message) {
    boolean methodFound = false;

    for (Method method : this.getClass().getDeclaredMethods()) {
      ReflectionName reflectionNameAnnotation = method.getAnnotation(ReflectionName.class);

      if (reflectionNameAnnotation == null || !reflectionNameAnnotation.value().equals(message.getCommand())) {
        continue;
      }

      // TODO Check if this also works on public methods
      method.setAccessible(true);

      Method implementerGetInstance;
      Object implementerInst;
      try {
        implementerGetInstance = this.getClass().getDeclaredMethod("getInstance");
        implementerInst = implementerGetInstance.invoke(null);
      } catch (NoSuchMethodException e) {
        e.printStackTrace(); // FIXME

        return;
      } catch (IllegalAccessException e) {
        e.printStackTrace(); // FIXME

        return;
      } catch (InvocationTargetException e) {
        e.printStackTrace(); // FIXME

        return;
      }

      try {
        methodFound = true;

        method.invoke(implementerInst, node, message);

        break;
      } catch (IllegalAccessException e) {
        e.printStackTrace(); // FIXME
      } catch (InvocationTargetException e) {
        e.printStackTrace(); // FIXME
      }
    }

    if (!methodFound) {
      logger.warn("Method to handle the message could not be found. Message will be ignored.");
    }
  }
}
