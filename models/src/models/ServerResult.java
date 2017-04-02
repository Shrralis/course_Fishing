package models;

import com.sun.istack.internal.NotNull;

import java.io.Serializable;

/**
 * Created by shrralis on 2/19/17.
 */
public class ServerResult implements Serializable {
    private int iResult = 0;

    private String sMessage = "success";

    private List mObjects;

    private ServerResult(int result, @NotNull String message) {
        iResult = result;
        sMessage = message;
        mObjects = null;
    }

    private ServerResult(List objects) {
        if (objects != null) {
            mObjects = objects;
            iResult = 0;
        } else {
            mObjects = null;
            iResult = -1;
        }
    }

    public static ServerResult create(int result, @NotNull String message) {
        return new ServerResult(result, message);
    }

    public static ServerResult create(List objects) {
        return new ServerResult(objects);
    }

    public int getResult() {
        return iResult;
    }

    public String getMessage() {
        return sMessage;
    }

    public List getObjects() {
        return mObjects;
    }
}
