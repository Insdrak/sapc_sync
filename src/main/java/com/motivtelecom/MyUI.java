package com.motivtelecom;

import com.motivtelecom.dao.FidExtra;
import com.motivtelecom.dao.JDBCQuerries;
import com.motivtelecom.dao.SAPCObject;
import com.motivtelecom.dao.SAPCQuerries;
import com.motivtelecom.sec.IIS2TOMCATMediator;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    private final static Logger logger = Logger.getLogger(MyUI.class);
    private String userName;
    private JDBCQuerries jdbcQuerries;
    private SAPCQuerries sapcQuerries;
    private List<SAPCObject> sapcList = new ArrayList<>();
    private List<FidExtra> extraList = new ArrayList<>();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Integer session_timeout = 18000;
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(session_timeout); //60*60*5
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-config.xml");
        jdbcQuerries = new JDBCQuerries(applicationContext);
        sapcQuerries = new SAPCQuerries(applicationContext);
        userName = vaadinRequest.getParameter("authUser");
        VaadinSession.getCurrent().setAttribute("UserName",userName);
        if (IIS2TOMCATMediator.check_access(userName)) {
            VerticalLayout mainPageLayout = new VerticalLayout();
            Panel panelMainLayout = new Panel("Сервис по удалению лишней квоты");
            Label labelPick = new Label("<b><i>Выберите необходимый УЗ</i></b>", ContentMode.HTML);
            final TextField textFieldPick = new TextField();
            Button buttonMainProcess = new Button("Синхронизировать квоту");
            buttonMainProcess.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    String result = syncBSS2SAPC(textFieldPick.getValue());
                    logger.info(result);
                }
            });

            mainPageLayout.setMargin(true);
            mainPageLayout.addComponent(labelPick);
            mainPageLayout.addComponent(textFieldPick);
            mainPageLayout.addComponent(buttonMainProcess);
            panelMainLayout.setContent(mainPageLayout);
            setContent(panelMainLayout);
        }
        else {
            String access_denied = "Доступ к данному приложению ограничен. Пожалуйста обратитесь в отдел АСР";
            logger.warn("Access denied to user "+userName);
            Notification.show(userName + ": " + access_denied, Notification.Type.ERROR_MESSAGE);
        }
    }

    private String syncBSS2SAPC (String ouz){
        extraList.clear();
        sapcList.clear();
        extraList = jdbcQuerries.getFid4accrec(ouz);
        sapcList = sapcQuerries.getParsedSAPCDATAByNumber(ouz);
        fidComparator(extraList,sapcList,ouz);
        return "ok";
    }

    private void fidComparator(List<FidExtra> fidExtras, List<SAPCObject> sapcObjects, String in_number){
        // 1 of all 70 is ignored
        List<SAPCObject> sapcList = new ArrayList<>();
        List<FidExtra> bssList = new ArrayList<>();
        String resultNotification = "";
        for (SAPCObject sapcObject:sapcObjects){
            if (sapcObject.getFid().intValue()!=70&&sapcObject.getFid().intValue()!=1701&&sapcObject.getFid().intValue()!=1702){
                sapcList.add(sapcObject);
            }
        }
        for (FidExtra fid:fidExtras){
            if (fid.getF_id().intValue()!=70){
                bssList.add(fid);
            }
        }
        for (SAPCObject sapcObject:sapcList){
            for (FidExtra fid:bssList){
                if (fid.getF_id().compareTo(sapcObject.getFid()) == 0 ){
                    sapcObject.setEntryfound(true);
                }
            }
            if (!sapcObject.getEntryfound()){
                resultNotification = resultNotification.concat(setSAPCObjectToOFF(sapcObject,in_number));
            }
        }
        if (resultNotification.equals("")) resultNotification = "Услуги корректны";
        Notification.show(resultNotification);
    }

    private String setSAPCObjectToOFF (SAPCObject sapcObject,String in_number){
        String res = sapcQuerries.addSAPCEntry(in_number,sapcObject.getFid()+":"+sapcObject.getPriority(),true);
        String outNotifivation = "";
        if (res.equals("ok")){
            logger.info("Удаление услуги "+sapcObject.getFid()+" для номера "+in_number+" произведено успешно");
            outNotifivation = outNotifivation.concat("Удаление услуги "+jdbcQuerries.getServNameByFid(sapcObject.getFid())+" для номера "+in_number+" произведено успешно\n\r");
            jdbcQuerries.whriteLogtab(
                    "USER::"+userName+";"+
                    "SAPC_DELETE_ACC::"+in_number+";"+
                    "SAPC_DELETE_FID::"+sapcObject.getFid().toString()
            );
        }
        else {
            outNotifivation = outNotifivation.concat("Во время удаления услуги "+jdbcQuerries.getServNameByFid(sapcObject.getFid())+" для номера "+in_number+" произошла ошибка, удаление не произведено");
        }
        return outNotifivation;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
