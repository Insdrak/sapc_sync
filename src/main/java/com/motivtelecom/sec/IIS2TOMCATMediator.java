package com.motivtelecom.sec;

import com.motivtelecom.MyUI;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Properties;

public class IIS2TOMCATMediator {

    private static final Hashtable<String,String> env = new Hashtable<>();
    private static Properties ldapProperties = new Properties();
    private final static Logger logger = Logger.getLogger(IIS2TOMCATMediator.class);

    static public boolean  check_access (String in_login_data){
        try {
            loadConfig();
            if (in_login_data == null) return false;
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapProperties.getProperty("ldap.url"));
            env.put(Context.SECURITY_PRINCIPAL, ldapProperties.getProperty("ldap.user"));
            env.put(Context.SECURITY_CREDENTIALS, ldapProperties.getProperty("ldap.password"));
            LdapContext ctx = new InitialLdapContext(env, null);
            ctx.setRequestControls(null);
            SearchControls searchControls = new SearchControls();
            searchControls.setTimeLimit(30000);
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration answer = ctx.search(ldapProperties.getProperty("ldap.groupPath"),"(cn=*)",searchControls);
            while (answer.hasMore()){
                SearchResult searchResult = (SearchResult) answer.next();
                BasicAttributes basicAttributes = (BasicAttributes) searchResult.getAttributes();
                Attribute attribute = basicAttributes.get("member");
                for (int i=0;i<attribute.size();i++){
                    String name = getSAMAccountName((String) attribute.get(i),ctx,searchControls);
                    if (name.matches(in_login_data)) return true;
                }
            }
            return false;
        } catch (NamingException e) {
            logger.error(e);
            e.printStackTrace();
            return false;
        }
    }

    private static String getSAMAccountName(String in_ldap_path, LdapContext ctx, SearchControls searchControls){
        try {
            NamingEnumeration answer = ctx.search(in_ldap_path,"(cn=*)",searchControls);
            SearchResult searchResult = (SearchResult) answer.next();
            BasicAttributes basicAttributes = (BasicAttributes) searchResult.getAttributes();
            Attribute attribute = basicAttributes.get("samaccountname");
            return (String) attribute.get();
        } catch (NamingException e) {
            logger.error(e);
            e.printStackTrace();
            return "";
        }
    }

    private static void loadConfig (){
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(MyUI.class.getProtectionDomain().getCodeSource().getLocation().getPath().concat("ldap.properties"));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"windows-1251");
            try{
                ldapProperties.load(inputStreamReader);
            }
            finally {
                inputStreamReader.close();
                fileInputStream.close();
            }
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }
}