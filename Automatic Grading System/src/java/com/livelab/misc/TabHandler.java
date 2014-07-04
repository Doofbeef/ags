package com.livelab.misc;

import java.io.Serializable;
import java.util.Map;
import javax.el.ELException;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import org.primefaces.component.tabview.TabView;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.event.TabChangeEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.component.html.HtmlCommandButton;

@ManagedBean
@SessionScoped
public class TabHandler implements Serializable {

    private int mainTabs;
    private int studentTabs;
    private int instructorTabs;
    @ManagedProperty(value = "#{studentLogin}")
    private com.livelab.login.studentLogin studentLogin;
    @ManagedProperty(value = "#{instructorLogin}")
    private com.livelab.login.instructorLogin instructorLogin;

    public void setStudentLogin(com.livelab.login.studentLogin studentLogin) {
        this.studentLogin = studentLogin;
    }

    public void setInstructorLogin(com.livelab.login.instructorLogin instructorLogin) {
        this.instructorLogin = instructorLogin;
    }

    public TabHandler() {
        mainTabs = 0;
        studentTabs = 0;
        instructorTabs = 1;
    }

    public int getMainTabs() {
        try {
            int change = ((TabView) (FacesContext.getCurrentInstance().getViewRoot().findComponent("tabview1"))).getActiveIndex();
            if (change == 0) {
                if (studentLogin.getIsLoggedIn() == true) {
                    ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:slob1")).setRendered(true);
                }
                ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:ilob1")).setRendered(false);
            } else if (change == 1) {
                if (instructorLogin.getIsLoggedIn() == true) {
                    ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:ilob1")).setRendered(true);
                }
                ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:slob1")).setRendered(false);
            } else {
                ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:ilob1")).setRendered(false);
                ((HtmlCommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:slob1")).setRendered(false);
            }
        } catch (ELException e) {
        } catch (NullPointerException e) {
        }
        return mainTabs;
    }

    public void setMainTabs(int mainTabs) {
        this.mainTabs = mainTabs;
    }

    public int getStudentTabs() {
        return studentTabs;
    }

    public void setStudentTabs(int studentTabs) {
        this.studentTabs = studentTabs;
    }

    public int getInstructorTabs() {
        return instructorTabs;
    }

    public void setInstructorTabs(int instructorTabs) {
        this.instructorTabs = instructorTabs;
    }

    public void onMTabChange(TabChangeEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) event.getComponent();
        String activeIndexValue = params.get(tabView.getClientId(context) + "_tabindex");

        this.mainTabs = Integer.parseInt(activeIndexValue);
        refresh();
    }

    public void onSTabChange(TabChangeEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) event.getComponent();
        String activeIndexValue = params.get(tabView.getClientId(context) + "_tabindex");

        this.studentTabs = Integer.parseInt(activeIndexValue);
        refresh();
    }

    public void onITabChange(TabChangeEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) event.getComponent();
        String activeIndexValue = params.get(tabView.getClientId(context) + "_tabindex");

        this.instructorTabs = Integer.parseInt(activeIndexValue);
        refresh();
    }

    public void onMTabChange(AjaxBehaviorEvent event) {
    }

    public void onSTabChange(AjaxBehaviorEvent event) {
    }

    public void onITabChange(AjaxBehaviorEvent event) {
    }

    public void refresh() {
        studentLogin.refresh();
        instructorLogin.refresh();
    }
}
