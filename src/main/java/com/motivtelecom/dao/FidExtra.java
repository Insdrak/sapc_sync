package com.motivtelecom.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;

@SuppressWarnings("unused")
public class FidExtra {
    private BigDecimal serv_accrec_id;
    private BigDecimal serv2bal_id;
    private BigDecimal accrec_id;
    private Timestamp accrec_dattim1;
    private Timestamp accrec_dattim2;
    private BigDecimal balance_id;
    private BigDecimal p_serv2bal_id;
    private BigDecimal serv_id;
    private BigDecimal idn_serv2bal_id;
    private BigDecimal tar_serv2bla_id;
    private BigDecimal serv2f_mapi_id;
    private BigDecimal f_id;
    private BigDecimal state_flags;
    private Boolean isCurrentlyOn = false;

    public BigDecimal getServ_accrec_id() {
        return serv_accrec_id;
    }

    public void setServ_accrec_id(BigDecimal serv_accrec_id) {
        this.serv_accrec_id = serv_accrec_id;
    }

    public BigDecimal getServ2bal_id() {
        return serv2bal_id;
    }

    public void setServ2bal_id(BigDecimal serv2bal_id) {
        this.serv2bal_id = serv2bal_id;
    }

    public BigDecimal getAccrec_id() {
        return accrec_id;
    }

    public void setAccrec_id(BigDecimal accrec_id) {
        this.accrec_id = accrec_id;
    }

    public Timestamp getAccrec_dattim1() {
        return accrec_dattim1;
    }

    public void setAccrec_dattim1(Timestamp accrec_dattim1) {
        this.accrec_dattim1 = accrec_dattim1;
    }

    public Timestamp getAccrec_dattim2() {
        return accrec_dattim2;
    }

    public void setAccrec_dattim2(Timestamp accrec_dattim2) {
        this.accrec_dattim2 = accrec_dattim2;
    }

    public BigDecimal getBalance_id() {
        return balance_id;
    }

    public void setBalance_id(BigDecimal balance_id) {
        this.balance_id = balance_id;
    }

    public BigDecimal getP_serv2bal_id() {
        return p_serv2bal_id;
    }

    public void setP_serv2bal_id(BigDecimal p_serv2bal_id) {
        this.p_serv2bal_id = p_serv2bal_id;
    }

    public BigDecimal getServ_id() {
        return serv_id;
    }

    public void setServ_id(BigDecimal serv_id) {
        this.serv_id = serv_id;
    }

    public BigDecimal getIdn_serv2bal_id() {
        return idn_serv2bal_id;
    }

    public void setIdn_serv2bal_id(BigDecimal idn_serv2bal_id) {
        this.idn_serv2bal_id = idn_serv2bal_id;
    }

    public BigDecimal getTar_serv2bla_id() {
        return tar_serv2bla_id;
    }

    public void setTar_serv2bla_id(BigDecimal tar_serv2bla_id) {
        this.tar_serv2bla_id = tar_serv2bla_id;
    }

    public BigDecimal getServ2f_mapi_id() {
        return serv2f_mapi_id;
    }

    public void setServ2f_mapi_id(BigDecimal serv2f_mapi_id) {
        this.serv2f_mapi_id = serv2f_mapi_id;
    }

    public BigDecimal getF_id() {
        return f_id;
    }

    public void setF_id(BigDecimal f_id) {
        this.f_id = f_id;
    }

    public BigDecimal getState_flags() {
        return state_flags;
    }

    public void setState_flags(BigDecimal state_flags) {
        this.state_flags = state_flags;
    }

    public Boolean getIsCurrentlyOn() {
        return isCurrentlyOn;
    }

    public void setIsCurrentlyOn(Boolean isCurrentlyOn) {
        this.isCurrentlyOn = isCurrentlyOn;
    }
}
