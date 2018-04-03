package com.motivtelecom.dao;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class JDBCQuerries {
    private final static Logger logger = Logger.getLogger(JDBCQuerries.class);

    private final JdbcTemplate template;

    public JDBCQuerries(ApplicationContext context) {
        template = (JdbcTemplate) context.getBean("main");
    }

    public void whriteLogtab(final String message) {
        String sql =
                " BEGIN " +
                " sysbee.sk_logtab_pkg.writelogtab(incid => 'ASR_AUTO', insubcid =>'SAPC_SYNC' , indat => sysdate, indescr => ? ); " +
                " END; ";

        try (Connection connection = template.getDataSource().getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setNString(1, message);
                preparedStatement.execute();
            } catch (SQLException e1) {
                logger.error(e1);
                e1.printStackTrace();
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    private String getClientIdByCust (String inCust){
        String sql =
            "select "+
            "ci.client_id "+
            "from "+
            "sysbee.cust cu, "+
            "sysbee.client ci "+
            "where "+
            "cu.contracttype_id = ? "+
            "and "+
            "cu.contract = ? "+
            "and "+
            "cu.client_id = ci.client_id ";
        try (Connection connection = template.getDataSource().getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                String[] splitcust = inCust.split("-");
                preparedStatement.setNString(1,splitcust[0]);
                preparedStatement.setNString(2,splitcust[1]);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.first()){
                    return resultSet.getNString(1);
                }
                else return "error";
            } catch (SQLException e1) {
                logger.error(e1);
                e1.printStackTrace();
                return "error";
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
            return "error";
        }
    }

    public List<FidExtra> getFid4accrec(String ouz) {
        //getting serv_id s which should be used ic sapc
        String sql =
            " SELECT DISTINCT "+
            " S2F.F_ID FID, "+
            " S2B.SERV_ID SERV, "+
            " S2B.SERV2BAL_ID S2BID "+
            " FROM "+
            " SYSBEE.ACCREC A, "+
            " SYSBEE.SERV_ACCREC SA, "+
            " SYSBEE.SERV2BAL    S2B, "+
            " SYSBEE.SERV2F_MAPI S2F "+
            " WHERE "+
            " A.NAME = ? "+
            " AND A.ACCREC_ID = SA.ACCREC_ID "+
            " AND SA.SERV2BAL_ID = S2B.P_SERV2BAL_ID "+
            " AND SYSDATE BETWEEN S2B.DATTIM1 AND S2B.DATTIM2 "+
            " AND SYSDATE BETWEEN SA.DATTIM1 AND SA.DATTIM2 "+
            " AND S2B.SERV_ID = S2F.SERV_ID "+
            " AND EXISTS "+
            " (SELECT 1 "+
            " FROM SYSBEE.FID_RULE C "+
            " WHERE "+
            " C.R_CTRL_DEV_ID  = 22 "+
            " AND C.F_ID  = S2F.F_ID ) ";
            /*" (SELECT 1 "+
            " FROM SYSBEE.F_MAPI_CDV_SUBST C "+
            " WHERE "+
            " C.SUBST_CTRL_DEV_ID  = 22 "+
            " AND C.F_ID  = S2F.F_ID "+
            " AND SYSDATE BETWEEN C.DATTIM1 AND C.DATTIM2) ";*/

        try (Connection connection = template.getDataSource().getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setNString(1, ouz);
                ResultSet resultSet = preparedStatement.executeQuery();
                ArrayList<FidExtra> outRes = new ArrayList<>();
                while (resultSet.next()){
                    FidExtra fidExtra = new FidExtra();
                    fidExtra.setF_id(resultSet.getBigDecimal(1));
                    fidExtra.setServ_id(resultSet.getBigDecimal(2));
                    fidExtra.setServ2bal_id(resultSet.getBigDecimal(3));
                    outRes.add(fidExtra);
                }
                return outRes;
            } catch (SQLException e1) {
                logger.error(e1);
                e1.printStackTrace();
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        return null;
    }

    public String getServNameByFid(BigDecimal fid) {
        String sql =
            " select "+
            " s.name "+
            " from "+
            " sysbee.serv2f_mapi s2fm, "+
            " sysbee.serv s "+
            " where "+
            " s2fm.f_id = ? "+
            " and "+
            " sysdate between s2fm.dattim1 and s2fm.dattim2 "+
            " and "+
            " s2fm.serv_id = s.serv_id " +
            " and rownum = 1 ";

        try (Connection connection = template.getDataSource().getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setBigDecimal(1, fid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    return resultSet.getNString(1);
                }
                return "";
            } catch (SQLException e1) {
                logger.error(e1);
                e1.printStackTrace();
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        return fid.toString();
    }
}