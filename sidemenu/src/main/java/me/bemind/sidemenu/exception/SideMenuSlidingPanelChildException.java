package me.bemind.sidemenu.exception;

/**
 * Created by debug on 19/11/15.
 */
public class SideMenuSlidingPanelChildException extends RuntimeException {


    public static final String LIMIT_CHILD_STRING = "Sliding panel must have one child";

    public SideMenuSlidingPanelChildException(String detailMessage) {
        super(detailMessage);
    }
}
