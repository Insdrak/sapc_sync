package com.motivtelecom.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;

import javax.naming.InvalidNameException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapName;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@SuppressWarnings("SpellCheckingInspection")
public class SAPCQuerries {

    private LdapTemplate template;

    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SAPCQuerries.class);
    public SAPCQuerries(ApplicationContext context) {
        template = (LdapTemplate) context.getBean("ldapTemplateSAPC");
    }

    public List<SAPCObject> getParsedSAPCDATAByNumber (String in_number){
        List<SAPCObject> res = new ArrayList<>();
        String unparsed_sapc_data = getSAPCDataByNumber(in_number);
        if(unparsed_sapc_data == null) return res;
        String[] data_to_parse = getSAPCDataByNumber(in_number).split(",");
        for (String d:data_to_parse){
            SAPCObject sapcObject = new SAPCObject();
            String[] data = d.split(":");
            DecimalFormat format = new DecimalFormat("");
            format.setParseBigDecimal(true);
            if (data[0].matches("Autoprovisioned")||data[0].matches("motivtelecom")||data[0].matches("redir_limit")){
                try {
                    sapcObject.setFid((java.math.BigDecimal) format.parse("70"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    sapcObject.setFid((java.math.BigDecimal) format.parse(data[0]));
                } catch (ParseException e) {
                    try {
                        sapcObject.setFid((java.math.BigDecimal) format.parse("70"));
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            try {
                sapcObject.setPriority((java.math.BigDecimal) format.parse(data[1]));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            res.add(sapcObject);
        }
        return res;
    }

    /*
        Attribute attr = new BasicAttribute("extensionAttribute»,»2601");
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        ldapTemplateWrite.modifyAttributes("CN=ntd,DC=motiv",new ModificationItem[] { item });
        */
    private String getSAPCDataByNumber(String in_number){
        //79502021797
        //79000409126
        in_number = "7".concat(in_number);
        LdapQuery query = query()
                .where("EPC-SubscriberId")
                .is(in_number);

        try {
            List<String> list = template.search(query, new AttributesMapper<String>() {
                public String mapFromAttributes(Attributes attrs) {
                    Attribute attribute;
                    try {
                        attribute = attrs.get("epc-groupids");
                        String res = "";
                        for (int i=0;i<attribute.size();i++){
                            res=res.concat(attribute.get(i).toString())+",";
                        }
                        return res;
                    } catch (Exception e) {
                        System.out.println("getSAPCDataByNumber:EPC-SubscriberId:" + e.getMessage());
                    }
                    return "";
                }
            });
            return list.get(0);
        } catch (Exception e) {
            return null;
        }
    }
    public String addSAPCEntry (String number_name,String addition,Boolean delete){
        //5349:300
        //EPC-SubscriberId=79000409126,EPC-SubscribersName=EPC-Subscribers,applicationName=EPC-EpcNode,nodeName=sapc1
        try{
            LdapName name = null;
            try {
                name = new LdapName("EPC-SubscriberId=7"+number_name+",EPC-SubscribersName=EPC-Subscribers");
            } catch (InvalidNameException e) {
                e.printStackTrace();
            }
            Attribute attr = new BasicAttribute("EPC-GroupIds",addition);
            ModificationItem item = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
            if (delete) item = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr);
            template.modifyAttributes(name,new ModificationItem[] { item });
            return "ok";
        }
        catch (Exception e){
            logger.error(e);
            return e.toString();
        }
    }
}
