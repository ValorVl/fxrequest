package org.sincore.fxrequest.utils;

import org.controlsfx.dialog.ExceptionDialog;

public class AppExceptionDialog {

    private final ExceptionDialog exceptionDialog;

    public AppExceptionDialog(Throwable e, String msg){
        exceptionDialog = new ExceptionDialog(e);
        if (msg != null)
            exceptionDialog.setContentText(msg);
    }

    public void show(){
        exceptionDialog.showAndWait();
    }

}
